package com.be.byeoldam.domain.notification.event;

import com.be.byeoldam.domain.bookmark.model.Bookmark;
import com.be.byeoldam.domain.bookmark.repository.BookmarkRepository;
import com.be.byeoldam.domain.notification.NotificationRepository;
import com.be.byeoldam.domain.notification.dto.NotificationMessage;
import com.be.byeoldam.domain.notification.model.BookmarkNotification;
import com.be.byeoldam.domain.notification.model.InviteNotification;
import com.be.byeoldam.domain.sharedcollection.model.SharedCollection;
import com.be.byeoldam.domain.sharedcollection.repository.SharedCollectionRepository;
import com.be.byeoldam.domain.user.model.User;
import com.be.byeoldam.domain.user.repository.UserRepository;
import com.be.byeoldam.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SharedCollectionRepository sharedCollectionRepository;
    private final BookmarkRepository bookmarkRepository;

    @RabbitListener(queues = "notification.queue")
    public void receiveNotification(NotificationMessage message) {

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
}
