package com.be.byeoldam.domain.bookmark.model;

import com.be.byeoldam.common.entity.BaseTimeEntity;
import com.be.byeoldam.domain.common.model.BookmarkUrl;
import com.be.byeoldam.domain.personalcollection.model.PersonalCollection;
import com.be.byeoldam.domain.sharedcollection.model.SharedCollection;
import com.be.byeoldam.domain.user.model.User;
import com.be.byeoldam.exception.CustomException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="bookmarks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 북마크와 북마크 링크 관계에서 북마크는 N(1:N)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = false)
    private BookmarkUrl bookmarkUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="personal_collection_id")
    private PersonalCollection personalCollection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="shared_collection_id")
    private SharedCollection sharedCollection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(name="priority", nullable=false)
    private boolean priority;

    @Column(name="is_read", nullable=false)
    private boolean isRead;

    @PrePersist
    @PreUpdate
    private void validateCollections() {
        if ((personalCollection == null && sharedCollection == null) ||
                (personalCollection != null && sharedCollection != null)) {
            throw new CustomException("Bookmark에는 personalCollection 또는 sharedCollection 중 하나만 있어야 합니다.");
        }
    }

    @Builder
    private Bookmark(BookmarkUrl bookmarkUrl, PersonalCollection personalCollection, SharedCollection sharedCollection, User user, boolean priority, boolean isRead) {
        this.bookmarkUrl = bookmarkUrl;
        this.personalCollection = personalCollection;
        this.sharedCollection = sharedCollection;
        this.user = user;
        this.priority = priority;
        this.isRead = isRead;
    }

    // 개인컬렉션에 추가하는 북마크
    public static Bookmark createPersonalBookmark(BookmarkUrl url, User user, PersonalCollection collection) {
        return Bookmark.builder()
                .bookmarkUrl(url)
                .personalCollection(collection)
                .sharedCollection(null)
                .user(user)
                .priority(false)
                .isRead(false)
                .build();
    }

    // 공유컬렉션에 추가하는 북마크
    public static Bookmark createSharedBookmark(BookmarkUrl url, User user, SharedCollection collection) {
        return Bookmark.builder()
                .bookmarkUrl(url)
                .personalCollection(null)
                .sharedCollection(collection)
                .user(user)
                .priority(false)
                .isRead(false)
                .build();
    }

    public void updatePersonalCollection(PersonalCollection collection) {
        this.personalCollection = collection;
    }

    public void updateSharedCollection(SharedCollection collection) {
        this.sharedCollection = collection;
    }

    // 복사 메서드
    public Bookmark copy() {
        return Bookmark.builder()
                .bookmarkUrl(this.bookmarkUrl)
                .personalCollection(this.personalCollection)
                .sharedCollection(this.sharedCollection)
                .user(this.user)
                .priority(false)
                .isRead(false)
                .build();
    }

    public void updatePriority() {
        this.priority = !this.priority;
    }

    public void updateRead() {
        this.isRead = !this.isRead;
    }
}
