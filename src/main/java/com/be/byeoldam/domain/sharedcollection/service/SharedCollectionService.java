package com.be.byeoldam.domain.sharedcollection.service;

import com.be.byeoldam.domain.bookmark.BookmarkService;
import com.be.byeoldam.domain.bookmark.dto.TagDto;
import com.be.byeoldam.domain.bookmark.model.Bookmark;
import com.be.byeoldam.domain.bookmark.repository.BookmarkRepository;
import com.be.byeoldam.domain.bookmark.repository.BookmarkTagRepository;
import com.be.byeoldam.domain.notification.NotificationRepository;
import com.be.byeoldam.domain.notification.model.InviteNotification;
import com.be.byeoldam.domain.notification.model.Notification;
import com.be.byeoldam.domain.sharedcollection.dto.*;
import com.be.byeoldam.domain.sharedcollection.model.Role;
import com.be.byeoldam.domain.sharedcollection.model.SharedCollection;
import com.be.byeoldam.domain.sharedcollection.model.SharedUser;
import com.be.byeoldam.domain.sharedcollection.repository.SharedCollectionRepository;
import com.be.byeoldam.domain.sharedcollection.repository.SharedUserRepository;
import com.be.byeoldam.domain.tag.model.Tag;
import com.be.byeoldam.domain.tag.util.JsoupUtil;
import com.be.byeoldam.domain.tag.util.UrlPreview;
import com.be.byeoldam.domain.user.model.User;
import com.be.byeoldam.domain.user.repository.UserRepository;
import com.be.byeoldam.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SharedCollectionService {

    private final SharedCollectionRepository sharedCollectionRepository;

    private final SharedUserRepository sharedUserRepository;

    private final UserRepository userRepository;

    private final BookmarkRepository bookmarkRepository;

    private final BookmarkTagRepository bookmarkTagRepository;
    private final NotificationRepository notificationRepository;

    private final BookmarkService bookmarkService;

    // 공유컬렉션 생성
    // 예외 1. user_id 확인
    @Transactional
    public void createSharedCollection(SharedCollectionRequest sharedCollectionRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));
        SharedCollection collection = sharedCollectionRequest.toEntity();
        collection = sharedCollectionRepository.save(collection);

        SharedUser sharedUser = SharedUser.create(user, collection, Role.OWNER);
        sharedUserRepository.save(sharedUser);
    }

    // 공유컬렉션 목록 조회
    @Transactional(readOnly = true)
    public List<SharedCollectionResponse> getSharedCollection(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        List<SharedUser> sharedUsers = sharedUserRepository.findByUser(user);

        List<SharedCollection> collections = sharedUsers.stream()
                .map(SharedUser::getSharedCollection)
                .toList();

        return collections.stream()
                .map(collection -> SharedCollectionResponse.of(collection.getId(), collection.getName()))
                .collect(Collectors.toList());
    }

    // 공유컬렉션 수정 - 이름
    // 예외 1. 이름을 바꾸려 하는 사용자와 컬렉션의 id가 일치하는지
    @Transactional
    public SharedCollectionResponse updateSharedCollection(SharedCollectionRequest sharedCollectionRequest, Long userId, Long collectionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        SharedCollection collection = sharedCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new CustomException("컬렉션을 찾을 수 없습니다."));

        SharedUser sharedUser = sharedUserRepository.findByUserAndSharedCollection(user, collection)
                .orElseThrow(() -> new CustomException("해당 컬렉션의 멤버가 아닙니다."));

        if(!sharedUser.getRole().equals(Role.OWNER)) {
            throw new CustomException("해당 권한은 방장만 가능합니다.");
        }

        collection.updateName(sharedCollectionRequest.getName());
        return SharedCollectionResponse.of(collection.getId(), collection.getName());
    }

    // 공유컬렉션 삭제
    // 방장만 가능
    // 예외 1. 방장이 아닌 경우
    @Transactional
    public void deleteSharedCollection(Long userId, Long collectionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        SharedCollection collection = sharedCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new CustomException("컬렉션을 찾을 수 없습니다."));

        SharedUser sharedUser = sharedUserRepository.findByUserAndSharedCollection(user, collection)
                .orElseThrow(() -> new CustomException("해당 컬렉션의 멤버가 아닙니다."));

        if (!sharedUser.getRole().equals(Role.OWNER)) {
            throw new CustomException("해당 권한은 방장만 가능합니다.");
        }


        List<Bookmark> bookmarkList = bookmarkRepository.findBySharedCollection(collection);

        for (Bookmark bookmark : bookmarkList) {
            bookmarkService.deleteBookmark(userId, bookmark.getId());
        }
        sharedUserRepository.deleteAllBySharedCollection(collection);

        sharedCollectionRepository.delete(collection);
    }

    // 공유컬렉션 상세 조회 - 북마크 목록 조회
    @Transactional(readOnly = true)
    public List<SharedBookmarkResponse> getCollectionBookmark(Long userId, Long collectionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(""));
        SharedCollection collection = sharedCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new CustomException(""));
        List<Bookmark> bookmarks = bookmarkRepository.findBySharedCollection(collection);
        return makeBookmarkResponse(bookmarks);
    }

    // 공유컬렉션 멤버 관리 - 초대
    // 초대 알림에서 수락을 누르면 이쪽으로 넘어옴
    // 예외 1 : 이미 초대한 멤버를 또 초대하려고 할 때
    @Transactional
    public void inviteNewMember(InviteAcceptRequest request, Long userId) {
        Notification notification = notificationRepository.findById(request.getNotificationId())
                .orElseThrow(() -> new CustomException("알림이 존재하지 않습니다."));
        if(!(notification instanceof InviteNotification)) {
            throw new CustomException("유효하지 않은 초대 알림입니다.");
        }

        InviteNotification inviteNotification = (InviteNotification) notification;

        // 로그인해있는 유저
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));
        // 초대를 받은 유저 확인
        User invitedUser = notification.getUser();
        // 초대한 공유컬렉션 확인
        SharedCollection collection = inviteNotification.getCollection();

        // 컬렉션에 있는 멤버인지 확인
        if (sharedUserRepository.findByUserAndSharedCollection(invitedUser, collection).isPresent()) {
            throw new CustomException("이미 초대된 유저입니다.");
        }

        SharedUser newSharedUser = SharedUser.create(invitedUser, collection, Role.MEMBER);
        sharedUserRepository.save(newSharedUser);

        notificationRepository.delete(inviteNotification);
    }

    // 공유멀렉션 멤버 관리 - 강퇴
    // 방장만 가능
    // 예외 1 - 방장 아니면
    // 예외 2 - 컬렉션 멤버가 아닌 사용자를 추방하려고 할 때
    @Transactional
    public void ejectMember(Long userId, Long collectionId, Long ejectedUserId) {
        // 존재하는 컬렉션인지 확인
        SharedCollection collection = sharedCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new CustomException("컬렉션을 찾을 수 없습니다."));

        // 방장 유저와 강퇴될 유저 확인
        User ownerUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));
        User ejectedUser = userRepository.findById(ejectedUserId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        // 방장이 해당 컬렉션에 있고, OWNER인지 확인
        SharedUser ownerSharedUser = sharedUserRepository.findByUserAndSharedCollection(ownerUser, collection)
                .orElseThrow(() -> new CustomException("해당 컬렉션의 멤버가 아닙니다."));
        if (!ownerSharedUser.getRole().equals(Role.OWNER)) {
            throw new CustomException("해당 권한은 방장만 가능합니다.");
        }

        // 강퇴될 멤버가 해당 컬렉션에 있고, MEMBER인지 확인
        SharedUser ejectedSharedUser = sharedUserRepository.findByUserAndSharedCollection(ejectedUser, collection)
                .orElseThrow(() -> new CustomException("해당 컬렉션의 멤버가 아닙니다."));
        if (!ejectedSharedUser.getRole().equals(Role.MEMBER)) {
            throw new CustomException("컬렉션의 멤버만 강퇴할 수 있습니다.");
        }

        sharedUserRepository.delete(ejectedSharedUser);
    }

    public List<CollectionMemberResponse> getMember(Long userId, Long collectionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(""));
        SharedCollection collection = sharedCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new CustomException(""));

        List<SharedUser> sharedUsers = sharedUserRepository.findBySharedCollection(collection);
        List<User> users = sharedUsers.stream().map(SharedUser::getUser).toList();

        if (!users.contains(user)) {
            throw new CustomException("");
        }

        return users.stream()
                .map(member -> new CollectionMemberResponse(
                        member.getId(),
                        member.getEmail(),
                        member.getNickname()
                )).toList();

    }

    private List<SharedBookmarkResponse> makeBookmarkResponse(List<Bookmark> bookmarks) {
        return bookmarks.stream()
                .map(bookmark -> {
                    UrlPreview preview = JsoupUtil.fetchMetadata(bookmark.getBookmarkUrl().getUrl());
                    List<TagDto> tagDtos = bookmarkTagRepository.findByBookmark(bookmark).stream()
                            .map(bookmarkTag -> {
                                Tag tag = bookmarkTag.getTag();
                                return TagDto.of(tag);
                            }).toList();
                    return SharedBookmarkResponse.of(bookmark, tagDtos, preview.getImageUrl(), preview.getTitle(), preview.getDescription());
                }).toList();
    }
}
