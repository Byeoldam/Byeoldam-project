package com.be.byeoldam.domain.bookmark.model;

import com.be.byeoldam.domain.tag.model.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="bookmark_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="bookmark_id")
    private Bookmark bookmark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tag_id")
    private Tag tag;

    private BookmarkTag(Bookmark bookmark, Tag tag) {
        this.bookmark = bookmark;
        this.tag = tag;
    }

    public static BookmarkTag create(Bookmark bookmark, Tag tag) {
        return new BookmarkTag(bookmark, tag);
    }
}
