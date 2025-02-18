package com.be.byeoldam.domain.bookmark.repository;

import com.be.byeoldam.domain.bookmark.model.Bookmark;
import com.be.byeoldam.domain.common.model.BookmarkUrl;
import com.be.byeoldam.domain.personalcollection.model.PersonalCollection;
import com.be.byeoldam.domain.sharedcollection.model.SharedCollection;
import com.be.byeoldam.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findById(Long id);

    @Query("SELECT b FROM Bookmark b WHERE b.user.id = :userId AND b.isRead = false AND b.createdAt <= :targetDate")
    List<Bookmark> findUnreadBookmarksByUserIdAndDate(@Param("userId") Long userId, @Param("targetDate") LocalDateTime targetDate);

    List<Bookmark> findByBookmarkUrlAndUser(BookmarkUrl bookmarkUrl, User user);

    List<Bookmark> findByUserAndPersonalCollection(User user, PersonalCollection collection);

    List<Bookmark> findByUserAndSharedCollection(User user, SharedCollection collection);

    List<Bookmark> findByUser(User user);

    @Query("SELECT b FROM Bookmark b WHERE b.user = :user AND b.isRead = false AND b.createdAt <= :thirtyDaysAgo")
    List<Bookmark> findOldBookmarksByUser(@Param("user") User user, @Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);

    @Query("SELECT b FROM Bookmark b WHERE b.user = :user AND b.priority = true")
    List<Bookmark> findPriorityByUser(@Param("user") User user);

    List<Bookmark> findBySharedCollection(SharedCollection collection);

    boolean existsByBookmarkUrlAndSharedCollection(BookmarkUrl bookmarkUrl, SharedCollection collection);
}
