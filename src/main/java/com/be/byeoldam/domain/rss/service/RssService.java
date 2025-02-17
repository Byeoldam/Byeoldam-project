package com.be.byeoldam.domain.rss.service;

import com.be.byeoldam.domain.rss.dto.RssLatestPostsResponse;
import com.be.byeoldam.domain.rss.dto.RssPostResponse;
import com.be.byeoldam.domain.rss.dto.RssSubscribeRequest;
import com.be.byeoldam.domain.rss.dto.UserRssResponse;
import com.be.byeoldam.domain.rss.model.Rss;
import com.be.byeoldam.domain.rss.model.UserRss;
import com.be.byeoldam.domain.rss.repository.RssRepository;
import com.be.byeoldam.domain.rss.repository.UserRssRepository;
import com.be.byeoldam.domain.user.model.User;
import com.be.byeoldam.domain.user.repository.UserRepository;
import com.be.byeoldam.exception.CustomException;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class RssService {

    private final RssRepository rssRepository;
    private final UserRssRepository userRssRepository;
    private final UserRepository userRepository;

    @Transactional
    public void subscribeRss(Long userId, RssSubscribeRequest rssSubscribeRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        String rssUrl = findRssUrl(rssSubscribeRequest.getSiteUrl()); // RSS URL 추출

        log.info("RSS URL: {}", rssUrl);

        Rss rss = rssRepository.findByRssUrl(rssUrl)
                .orElseGet(() -> {
                    String rssName = extractRssName(rssUrl);
                    return rssRepository.save(Rss.createRss(rssUrl, rssName));
                });

        // 이미 구독한 RSS인지 확인
        if (userRssRepository.existsByUserIdAndRssId(userId, rss.getId())) {
            throw new CustomException("이미 구독한 RSS입니다.");
        }

        userRssRepository.save(rssSubscribeRequest.toEntity(user, rss));
    }



    @Transactional
    public void unsubscribeRss(Long userId, Long rssId) {

        if (!userRepository.existsById(userId)) {
            throw new CustomException("사용자를 찾을 수 없습니다.");
        }

        if (!rssRepository.existsById(rssId)) {
            throw new CustomException("RSS를 찾을 수 없습니다.");
        }

        if (!userRssRepository.existsByUserIdAndRssId(userId, rssId)) {
            throw new CustomException("구독하지 않은 RSS입니다.");
        }

        userRssRepository.deleteByUserIdAndRssId(userId, rssId);
    }

    /**
     * 사용자의 RSS 구독 목록 조회 (새 글 확인)
     * @param userId
     * @return
     */
    @Transactional
    public List<UserRssResponse> getUserRssList(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new CustomException("사용자를 찾을 수 없습니다.");
        }

        List<UserRss> userRssList = userRssRepository.findByUserId(userId);

        for (UserRss userRss : userRssList) {
            try {
                // RSS 링크의 최신 글 제목 추출
                String currentTitle = extractLatestTitle(userRss.getRss().getRssUrl());

                if (currentTitle != null && !currentTitle.equals(userRss.getLatestTitle())) {
                    userRss.updateIsRead(false);
                }
            } catch (Exception e) {
                throw new CustomException("RSS 피드를 가져오는 중 오류가 발생했습니다.");
            }
        }

        return userRssList.stream()
                .map(userRss -> UserRssResponse.of(
                        userRss.getRss().getId(),
                        userRss.getRss().getName(),
                        userRss.isRead()
                ))
                .toList();
    }

    /**
     * 특정 RSS 최신 글 목록을 조회하는 메서드
     * @param userId
     * @param rssId
     * @return
     */
    @Transactional
    public RssLatestPostsResponse getRssLatestArticles(Long userId, Long rssId, Pageable pageable) {

        if (!userRepository.existsById(userId)) {
            throw new CustomException("사용자를 찾을 수 없습니다.");
        }

        Rss rss = rssRepository.findById(rssId)
                .orElseThrow(() -> new CustomException("RSS를 찾을 수 없습니다."));

        UserRss userRss = userRssRepository.findByUserIdAndRssId(userId, rssId)
                .orElseThrow(() -> new CustomException("구독하지 않은 RSS입니다."));


        String newestTitle = extractLatestTitle(rss.getRssUrl());

        if (newestTitle != null && pageable.getPageNumber() == 0) {
            userRss.updateTitles(newestTitle);
        }

        String latestTitleBeforeUpdate = userRss.getLatestTitle();
        String previousBeforeUpdate = userRss.getPreviousTitle();

        Page<RssPostResponse> latestArticles = fetchRssPosts(rss.getRssUrl(), latestTitleBeforeUpdate, previousBeforeUpdate, pageable);

        return RssLatestPostsResponse.of(
                rss.getId(),
                rss.getName(),
                latestArticles
        );
    }

    // RSS의 최신 글 목록을 가져오는 메서드
    public Page<RssPostResponse> fetchRssPosts(String rssUrl, String latestTitle, String previousTitle, Pageable pageable) {
        try {
            // RSS 피드 가져오기
            URL feedSource = new URL(rssUrl);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));

            List<SyndEntry> entries = feed.getEntries();

            if (entries.isEmpty()) {
                return Page.empty(pageable);
            }
//
//            if (previousTitle == null) {
//                return new PageImpl<>(
//                        entries.stream()
//                                .map(entry -> RssPostResponse.builder()
//                                        .title(entry.getTitle())
//                                        .url(entry.getLink())
//                                        .isRead(false) // 첫 구독 시, 모든 글을 새 글로 처리
//                                        .build()
//                                )
//                                .skip(pageable.getOffset())
//                                .limit(pageable.getPageSize())
//                                .toList(),
//                        pageable,
//                        entries.size()
//                );
//            }

            // latestTitle 해당하는 글의 publishedDate 가져오기
            LocalDateTime previousDateTime = (previousTitle != null) ?
                    entries.stream()
                        .filter(entry -> Objects.equals(entry.getTitle(), previousTitle))
                        .map(entry -> convertToLocalDateTime(entry.getPublishedDate()))
                        .findFirst()  // Optional로 반환됨
                        .orElse(null)
                    : null;

            // 페이징 처리
            List<RssPostResponse> pagedArticles = entries.stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .map(entry -> {
                        String title = entry.getTitle();
                        String link = entry.getLink();

                        LocalDateTime publishedDate = convertToLocalDateTime(entry.getPublishedDate());

                        boolean isRead = (previousDateTime != null && (publishedDate.isBefore(previousDateTime) || publishedDate.isEqual(previousDateTime)));

                        return RssPostResponse.builder()
                                .title(title)
                                .url(link)
                                .isRead(isRead)
                                .build();
                    }).toList();

            return new PageImpl<>(pagedArticles, pageable, entries.size());

        } catch (Exception e) {
            throw new CustomException("RSS 피드를 가져오는 중 오류가 발생했습니다.");
        }
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * RSS 이름 추출 메서드
     */
    public String extractRssName(String rssUrl) {
        try {
            URL feedSource = new URL(rssUrl);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));

            log.info("RSS 피드 제목: {}", feed.getTitle());

            return feed.getTitle(); // RSS 피드 제목 반환
        } catch (Exception e) {
            throw new CustomException("RSS 제목을 가져오는 중 오류가 발생했습니다.");
        }
    }

    /**
     * RSS 피드의 가장 최근 게시물 제목을 가져오는 메서드
     */
    public String extractLatestTitle(String rssUrl) {
        try {
            URL feedSource = new URL(rssUrl);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));

            return feed.getEntries().isEmpty() ? null : feed.getEntries().get(0).getTitle();
        } catch (Exception e) {
            throw new CustomException("RSS 피드를 가져오는 중 오류가 발생했습니다.");
        }
    }

    /**
     * RSS 구독 가능한 URL인지 확인하는 메서드
     */
    public boolean isSubscribed(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("link[type=application/rss+xml]");
            return !links.isEmpty();
        } catch (IOException e) {
            return false;
        }
    }


    /**
     * RSS URL 추출 메서드
     * @param siteUrl
     * @return
     */
    public String findRssUrl(String siteUrl) {
        try {
            Document doc = Jsoup.connect(siteUrl).get();
            Elements links = doc.select("link[type=application/rss+xml], link[type=application/atom+xml]");

            if (links.isEmpty()) {
                throw new IOException("No RSS URL found");
            }

            /**
             * 여기부터
             */

            List<String> rssUrls = links.stream()
                    .map(link -> {
                        String rssUrl = link.attr("href");
                        if (!rssUrl.startsWith("http")) {
                            rssUrl = siteUrl + (rssUrl.startsWith("/") ? rssUrl : "/" + rssUrl);
                        }
                        return rssUrl;
                    })
                    .toList();

            for (String rssUrl : rssUrls) {
                try {
                    // RSS 피드 제목을 정상적으로 가져올 수 있는지 확인
                    String rssName = extractRssName(rssUrl);
                    if (rssName != null) {
                        return rssUrl;
                    }
                } catch (CustomException e) {
                    log.warn("RSS URL {}에서 제목을 가져오지 못함, 다음 URL 시도", rssUrl);
                }
            }

        } catch (IOException e) {
            throw new CustomException("RSS URL을 찾을 수 없습니다.");
        }

        return null;
    }

    /**
     * RSS 목록 중 새 글이 있는지 확인하는 메서드
     */
    public boolean hasNewArticles(Long userId) {
        List<UserRss> userRssList = userRssRepository.findByUserId(userId);

        for (UserRss userRss : userRssList) {
            try {
                String currentTitle = extractLatestTitle(userRss.getRss().getRssUrl());

                if (currentTitle != null && !currentTitle.equals(userRss.getLatestTitle())) {
                    return true;
                }
            } catch (Exception e) {
                throw new CustomException("RSS 피드를 가져오는 중 오류가 발생했습니다.");
            }
        }

        return false;
    }
}
