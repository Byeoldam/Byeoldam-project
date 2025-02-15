package com.be.byeoldam.domain.personalcollection;

import com.be.byeoldam.domain.bookmark.BookmarkService;
import com.be.byeoldam.domain.bookmark.dto.TagDto;
import com.be.byeoldam.domain.bookmark.model.Bookmark;
import com.be.byeoldam.domain.bookmark.repository.BookmarkRepository;
import com.be.byeoldam.domain.bookmark.repository.BookmarkTagRepository;
import com.be.byeoldam.domain.personalcollection.dto.PersonalBookmarkListResponse;
import com.be.byeoldam.domain.personalcollection.dto.PersonalBookmarkResponse;
import com.be.byeoldam.domain.personalcollection.dto.PersonalCollectionRequest;
import com.be.byeoldam.domain.personalcollection.dto.PersonalCollectionResponse;
import com.be.byeoldam.domain.personalcollection.model.PersonalCollection;
import com.be.byeoldam.domain.personalcollection.repository.PersonalCollectionRepository;
import com.be.byeoldam.domain.tag.model.Tag;
import com.be.byeoldam.domain.user.model.User;
import com.be.byeoldam.domain.user.repository.UserRepository;
import com.be.byeoldam.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalCollectionService {

    private final UserRepository userRepository;
    private final PersonalCollectionRepository personalCollectionRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BookmarkTagRepository bookmarkTagRepository;
    private final BookmarkService bookmarkService;

    @Transactional
    public PersonalCollectionResponse createPersonalCollection(PersonalCollectionRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        validate(user, request.getName());
        PersonalCollection collection = request.toEntity(user);

        PersonalCollection collection1 = personalCollectionRepository.save(collection);
        return PersonalCollectionResponse.of(collection1.getId(), collection1.getName());
    }

    // 개인컬렉션 목록 조회
    @Transactional(readOnly = true)
    public List<PersonalCollectionResponse> getPersonalCollections(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));
        return personalCollectionRepository.findByUser(user).stream()
                .map(collection -> PersonalCollectionResponse.of(collection.getId(), collection.getName()))
                .toList();
    }

    // 개인컬렉션 이름 수정
    @Transactional
    public PersonalCollectionResponse updatePersonalCollection(PersonalCollectionRequest request, Long userId, Long collectionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        PersonalCollection collection = personalCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new CustomException("컬렉션을 찾을 수 없습니다."));

        if (!collection.getUser().getId().equals(userId)) {
            throw new CustomException("해당 컬렉션에 대한 권한이 없습니다.");
        }
        String newName = request.getName();
        validate(user, newName);
        collection.updateName(newName);
        return PersonalCollectionResponse.of(collectionId, newName);
    }

    // 개인컬렉션 삭제
    @Transactional
    public void deletePersonalCollection(Long userId, Long collectionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        PersonalCollection collection = personalCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new CustomException("컬렉션을 찾을 수 없습니다."));
        if (!collection.getUser().getId().equals(userId)) {
            throw new CustomException("해당 컬렉션에 대한 권한이 없습니다.");
        }

        List<Bookmark> bookmarkList = bookmarkRepository.findByUserAndPersonalCollection(user, collection);

        for (Bookmark bookmark : bookmarkList) {
            bookmarkService.deleteBookmark(userId, bookmark.getId());
        }

        personalCollectionRepository.delete(collection);
    }

    // 컬렉션에서 북마크 조회
    @Transactional(readOnly = true)
    public PersonalBookmarkListResponse getCollectionBookmark(Long userId, Long collectionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        PersonalCollection collection = personalCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new CustomException("컬렉션을 찾을 수 없습니다."));

        if (!collection.getUser().getId().equals(userId)) {
            throw new CustomException("해당 컬렉션에 대한 권한이 없습니다.");
        }

        List<Bookmark> bookmarks = bookmarkRepository.findByUserAndPersonalCollection(user, collection);

        return PersonalBookmarkListResponse.of(collectionId, makeBookmarkResponse(bookmarks));
    }

    // 개인 컬렉션 기능 - 30일 이상 보지 않은 컬렉션 조회
    @Transactional(readOnly = true)
    public List<PersonalBookmarkResponse> getLongUnreadBookmark(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Bookmark> bookmarks = bookmarkRepository.findOldBookmarksByUser(user, thirtyDaysAgo);
        return makeBookmarkResponse(bookmarks);
    }

    // 개인 컬렉션 기능 - 중요 북마크 컬렉션 조회
    @Transactional(readOnly = true)
    public List<PersonalBookmarkResponse> getPriorityBookmark(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));
        List<Bookmark> bookmarks = bookmarkRepository.findPriorityByUser(user);
        return makeBookmarkResponse(bookmarks);
    }

    private List<PersonalBookmarkResponse> makeBookmarkResponse(List<Bookmark> bookmarks) {
        return bookmarks.stream()
                .map(bookmark -> {
                    List<TagDto> tagDtos = bookmarkTagRepository.findByBookmark(bookmark).stream()
                            .map(bookmarkTag -> {
                                Tag tag = bookmarkTag.getTag();
                                return TagDto.of(tag);
                            }).toList();
                    return PersonalBookmarkResponse.of(bookmark, tagDtos);
                }).toList();
    }


    // 개인컬렉션 이름 검증(create, update)
    private void validate(User user, String name) {
        if (personalCollectionRepository.existsByUserIdAndName(user.getId(), name)) {
            throw new CustomException("같은 이름의 컬렉션이 이미 존재합니다.");
        }
    }
}
