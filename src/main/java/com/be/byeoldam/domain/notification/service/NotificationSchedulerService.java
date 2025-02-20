package com.be.byeoldam.domain.notification.service;

import com.be.byeoldam.domain.bookmark.model.Bookmark;
import com.be.byeoldam.domain.bookmark.repository.BookmarkRepository;
import com.be.byeoldam.domain.notification.NotificationRepository;
import com.be.byeoldam.domain.notification.dto.NotificationMessage;
import com.be.byeoldam.domain.notification.event.NotificationProducer;
import com.be.byeoldam.domain.notification.model.BookmarkNotification;
import com.be.byeoldam.domain.user.model.User;
import com.be.byeoldam.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationSchedulerService {

    private final NotificationProducer notificationProducer;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;

    /**
     * 매일 자정에 N일 지난 북마크 알림 생성
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void createBookmarkNotification() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            int alertDay = user.getAlertDay();
            LocalDateTime targetDate = LocalDateTime.now().minusDays(alertDay);

            // n일 지난 북마크 조회
            List<Bookmark> bookmarks = bookmarkRepository.findUnreadBookmarksByUserIdAndDate(user.getId(), targetDate);

            // 북마크 알림 생성
            for (Bookmark bookmark : bookmarks) {
                NotificationMessage message = new NotificationMessage(
                        user.getId(),
                        "BOOKMARK",
                        "북마크한 글이 " + alertDay + "일 지났습니다.",
                        bookmark.getId()
                );

                notificationProducer.sendNotification(message);
            }
        }
    }
}
