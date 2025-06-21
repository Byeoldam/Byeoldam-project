package com.be.byeoldam.domain.notification.service;

import com.be.byeoldam.domain.bookmark.model.Bookmark;
import com.be.byeoldam.domain.bookmark.repository.BookmarkRepository;
import com.be.byeoldam.domain.notification.NotificationRepository;
import com.be.byeoldam.domain.notification.dto.NotificationMessage;
import com.be.byeoldam.domain.notification.dto.NotificationResponse;
import com.be.byeoldam.domain.notification.model.BookmarkNotification;
import com.be.byeoldam.domain.notification.model.InviteNotification;
import com.be.byeoldam.domain.notification.model.Notification;
import com.be.byeoldam.domain.sharedcollection.model.SharedCollection;
import com.be.byeoldam.domain.sharedcollection.repository.SharedCollectionRepository;
import com.be.byeoldam.domain.user.model.User;
import com.be.byeoldam.domain.user.repository.UserRepository;
import com.be.byeoldam.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private final SharedCollectionRepository sharedCollectionRepository;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public void processNotification(NotificationMessage message) throws Exception{
        User user = userRepository.findById(message.getUserId())
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        if (message.getType().equals("BOOKMARK")) {

            Bookmark bookmark = bookmarkRepository.findById(message.getTargetId())
                    .orElseThrow(() -> new CustomException("북마크를 찾을 수 없습니다."));

            BookmarkNotification notification = BookmarkNotification.builder()
                    .user(user)
                    .message(message.getMessage())
                    .bookmark(bookmark) // 북마크 ID를 추가하려면 BookmarkRepository 필요
                    .build();

            notificationRepository.save(notification);

        } else if (message.getType().equals("INVITE")) {

            SharedCollection collection = sharedCollectionRepository.findById(message.getTargetId())
                    .orElseThrow(() -> new CustomException("공유 컬렉션을 찾을 수 없습니다."));

            InviteNotification notification = InviteNotification.builder()
                    .user(user)
                    .collection(collection)
                    .message(message.getMessage())
                    .build();

            notificationRepository.save(notification);
        }
    }
    /**
     * 사용자의 알림 목록을 조회하는 메서드
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        log.info("user: {}", user);

        List<Notification> notifications = notificationRepository.findByUser(user);

        log.info("notifications: {}", notifications);

        return notifications.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 사용자의 알림을 삭제하는 메서드
     * @param notificationId
     * @param userId
     */
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        Notification notification = notificationRepository.findByIdAndUser(notificationId, user)
                .orElseThrow(() -> new CustomException("해당 알림이 존재하지 않습니다."));

        if (!notification.getUser().getId().equals(userId)) {
            throw new CustomException("해당 알림에 대한 권한이 없습니다.");
        }

        notificationRepository.delete(notification);
    }

    /**
     * 사용자의 모든 알림을 삭제하는 메서드
     * @param userId
     */
    @Transactional
    public void deleteAllNotifications(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

        if (!user.getId().equals(userId)) {
            throw new CustomException("해당 알림에 대한 권한이 없습니다.");
        }

        notificationRepository.deleteByUser(user);
    }

    /**
     * Notification 객체를 NotificationResponse로 변환하는 메서드
     * @param notification
     * @return
     */
    private NotificationResponse toResponse(Notification notification) {

        String title = "";
        String message = "";
        String url = null;

        if (notification instanceof InviteNotification inviteNotification) { // 초대 알림일 경우
            title = inviteNotification.getCollection().getName();
            message = inviteNotification.getMessage();
            // 초대 알림은 url이 없음 (null)
        } else if (notification instanceof BookmarkNotification bookmarkNotification) { // 북마크 알림일 경우
            url = bookmarkNotification.getBookmark().getBookmarkUrl().getUrl();
            message = bookmarkNotification.getMessage();
            title = getTitleFromUrl(url);
        }


        return NotificationResponse.of(
                notification.getId(),
                notification.getType(),
                title,
                message,
                url,
                notification.getCreatedAt()
        );
    }

    private String getTitleFromUrl(String url) {
        try {
            Document document = Jsoup.connect(url)
                    .timeout(5000)
                    .get();

            // 페이지의 title 태그 값 가져오기
            return document.title();
        } catch (Exception e) {
            throw new CustomException("URL에서 제목을 가져오는데 실패하였습니다.");
        }
    }

}
