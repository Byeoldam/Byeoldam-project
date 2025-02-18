package com.be.byeoldam.domain.tag;

import com.be.byeoldam.domain.bookmark.dto.TagDto;
import com.be.byeoldam.domain.bookmark.model.Bookmark;
import com.be.byeoldam.domain.bookmark.repository.BookmarkTagRepository;
import com.be.byeoldam.domain.common.model.BookmarkUrl;
import com.be.byeoldam.domain.common.repository.BookmarkUrlRepository;
import com.be.byeoldam.domain.common.repository.TagBookmarkUrlRepository;
import com.be.byeoldam.domain.personalcollection.dto.PersonalBookmarkResponse;
import com.be.byeoldam.domain.tag.dto.RecommendedTagResponse;
import com.be.byeoldam.domain.tag.dto.RecommendedUrlResponse;
import com.be.byeoldam.domain.tag.dto.TagSearchResponse;
import com.be.byeoldam.domain.tag.dto.UserTagResponse;
import com.be.byeoldam.domain.tag.model.Tag;
import com.be.byeoldam.domain.tag.model.UserTag;
import com.be.byeoldam.domain.tag.repository.TagRepository;
import com.be.byeoldam.domain.tag.repository.UserTagRepository;
import com.be.byeoldam.domain.tag.util.JsoupUtil;
import com.be.byeoldam.domain.tag.util.UrlPreview;
import com.be.byeoldam.domain.user.model.User;
import com.be.byeoldam.domain.user.repository.UserRepository;
import com.be.byeoldam.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final UserTagRepository userTagRepository;
    private final TagBookmarkUrlRepository tagBookmarkUrlRepository;
    private final BookmarkUrlRepository bookmarkUrlRepository;
    private final BookmarkTagRepository bookmarkTagRepository;


    // 참조 횟수가 많은 상위 10개 태그를 조회    ->  회원 가입 후 나오는 관심 태그 저장 페이지
    @Transactional(readOnly = true)
    public RecommendedTagResponse getRecommendedTags() {
        List<Tag> tags = tagRepository.findTop10ByOrderByReferenceCountDesc();

        List<TagDto> tagDtoList = tags.stream()
                .map(tag -> TagDto.of(tag))
                .collect(Collectors.toList());

        return RecommendedTagResponse.of(tagDtoList);
    }


    // 사용자의 관심 태그 조회
    @Transactional(readOnly = true)
    public UserTagResponse getUserTags(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        List<TagDto> userTagList = userTagRepository.findByUser(user).stream()
                .map(userTag -> TagDto.of(userTag.getTag()))
                .collect(Collectors.toList());

        return UserTagResponse.of(userTagList);
    }


    // 사용자의 관심 태그 삭제 후, 추가하기  ->  마이페이지
    @Transactional
    public UserTagResponse addUserTag(Long userId, List<TagDto> tagDtoList) {

        // 사용자 관심 태그 이름들 얻기
        List<String> userTagNameList = tagDtoList
                .stream()
                .map(TagDto::getTagName)
                .toList();

        // 사용자 정보 얻기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        // 1. 해당 유저의 기존 UserTag 데이터 삭제
        userTagRepository.deleteByUser(user);
        // 2. tagList에 있는 태그들을 한 번에 조회
        List<Tag> existingTagList = tagRepository.findByNameIn(userTagNameList);
        // 3. 이미 존재하는 태그들을 Map으로 변환 (name -> Tag)
        Map<String, Tag> existingTagMap = existingTagList.stream()
                .collect(Collectors.toMap(Tag::getName, tag -> tag));

        List<Tag> newTagList = new ArrayList<>();
        for (TagDto userTag : tagDtoList) {
            if (!existingTagMap.containsKey(userTag.getTagName())) {
                // 4. 존재하지 않는 태그는 새로 생성
                Tag newTag = userTag.toEntity();
                newTagList.add(newTag);
            }
        }

        // 5. 새로운 태그들 한 번에 저장
        if (!newTagList.isEmpty()) {
            tagRepository.saveAll(newTagList);
            newTagList.forEach(tag -> existingTagMap.put(tag.getName(), tag)); // 새로 저장된 태그를 Map에 추가
        }

        // 6. 모든 태그를 UserTag 테이블에 저장 (중복 체크 없이 덮어쓰기)
        List<UserTag> userTags = existingTagMap.values().stream()
                .map(tag -> UserTag.create(user, tag))
                .collect(Collectors.toList());
        if (!userTags.isEmpty()) {
            userTagRepository.saveAll(userTags);
        }

        // 7. 저장된 UserTag 데이터를 활용해 TagDto 생성 (DB 조회 대신 메모리에서 처리)
        List<TagDto> userTagList = userTags.stream()
                .map(userTag -> TagDto.of(userTag.getTag()))
                .collect(Collectors.toList());

        return UserTagResponse.of(userTagList);
    }



    // 태그 기반 검색, 사용자 북마크 조회     ->    태그 기반 검색 페이지 (추후 개발 예정)
    @Transactional
    TagSearchResponse searchTag(long userId, String tagName, int cursorId, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("존재하지 않는 UserId 입니다."));

        Tag tag = tagRepository.findByName(tagName)
                .orElse(null);

        TagSearchResponse tagSearchResponseList = new TagSearchResponse();
        if (tag == null) {
            return tagSearchResponseList;
        }


        // 1. 사용자 북마크
        List<Bookmark> bookmarkUrlList = bookmarkTagRepository.findBookmarksByUserIdAndTagNameWithPaging(userId, tagName, cursorId * size, size);
        List<PersonalBookmarkResponse> personalBookmarkResponseList = bookmarkUrlList.stream()
                .map(bookmark -> {
                    List<TagDto> tagDtos = bookmarkTagRepository.findByBookmark(bookmark).stream()
                            .map(bookmarkTag -> {
                                Tag userTag = bookmarkTag.getTag();
                                return TagDto.of(userTag);
                            }).toList();
                    return PersonalBookmarkResponse.of(bookmark, tagDtos);
                }).toList();

        // 2. 추천 URL
        List<RecommendedUrlResponse> recommendedUrlList = tagBookmarkUrlRepository.findBookmarkUrlsByTagName(tagName, cursorId * size, size);

        TagSearchResponse response = TagSearchResponse.of(personalBookmarkResponseList, recommendedUrlList);
        return TagSearchResponse.of(personalBookmarkResponseList, recommendedUrlList);
    }


    // 태그 기반 검색, URL 추천(무한 스크롤)   ->    관심 태그 추천 페이지 / 태그 기반 검색 페이지
    @Transactional(readOnly = true)
    List<RecommendedUrlResponse> getBookmarkUrlsByTagName(String tagName, int cursorId, int size) {
        List<RecommendedUrlResponse> urlList = tagBookmarkUrlRepository.findBookmarkUrlsByTagName(tagName,cursorId*size, size);

        return urlList;
    }

}
