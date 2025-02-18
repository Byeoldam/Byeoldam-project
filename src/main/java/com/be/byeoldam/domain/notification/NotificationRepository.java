package com.be.byeoldam.domain.notification;

import com.be.byeoldam.domain.bookmark.model.Bookmark;
import com.be.byeoldam.domain.notification.model.Notification;
import com.be.byeoldam.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

        List<Notification> findByUser(User user);

        Optional<Notification> findByIdAndUser(Long notificationId, User user);

        void deleteByUser(User user);

        int countByUserId(Long userId);

        @Modifying
        @Query("DELETE FROM BookmarkNotification bn WHERE bn.bookmark.id = :bookmarkId")
        void deleteBookmarkNotification(@Param("bookmarkId") Long bookmarkId);
}
