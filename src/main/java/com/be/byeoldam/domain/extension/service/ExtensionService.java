package com.be.byeoldam.domain.extension.service;

import com.be.byeoldam.domain.extension.dto.ExtensionDataResponse;
import com.be.byeoldam.domain.extension.dto.ExtensionRequest;
import com.be.byeoldam.domain.notification.NotificationRepository;
import com.be.byeoldam.domain.personalcollection.PersonalCollectionService;
import com.be.byeoldam.domain.personalcollection.dto.PersonalCollectionResponse;
import com.be.byeoldam.domain.rss.service.RssService;
import com.be.byeoldam.domain.sharedcollection.dto.SharedCollectionResponse;
import com.be.byeoldam.domain.sharedcollection.service.SharedCollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ExtensionService {
    private final PersonalCollectionService personalCollectionService;
    private final RssService rssService;
    private final SharedCollectionService sharedCollectionService;
    private final NotificationRepository notificationRepository;
    private final KeywordExtractorService keywordExtractorService;

    /**
     * 익스텐션창 초기 데이터 가져오는 메서드
     */
    @Transactional(readOnly = true)
    public ExtensionDataResponse getLoadList(Long userId, ExtensionRequest request) {

        // Collection 리스트
        List<PersonalCollectionResponse> personalCollections = personalCollectionService.getPersonalCollections(userId);
        List<SharedCollectionResponse> sharedCollections = sharedCollectionService.getSharedCollection(userId);

        // url 핵심 키워드
        List<String> keywords = keywordExtractorService.extractKeywords(request);

        if (keywords == null || keywords.isEmpty()) {
            keywords = Collections.singletonList("기타");
            log.warn("키워드가 비어 있어 디폴트 키워드를 설정했습니다");
        }

        // RSS 구독 가능 여부
        boolean canSubscribeRss = rssService.isSubscribed(request.getSiteUrl());

        // 알림 갯수
        int notificationCnt = notificationRepository.countByUserId(userId);

        // 새 피드 여부
        boolean hasNewFeed = rssService.hasNewArticles(userId);

        // ExtensionDataResponse 객체 반환
        return new ExtensionDataResponse(
                personalCollections,
                sharedCollections,
                keywords,
                canSubscribeRss,
                notificationCnt,
                hasNewFeed
        );
    }

}
