package com.be.byeoldam.domain.bookmark.repository;

import com.be.byeoldam.domain.bookmark.model.Bookmark;
import com.be.byeoldam.domain.bookmark.model.BookmarkTag;
import com.be.byeoldam.domain.tag.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkTagRepository extends JpaRepository<BookmarkTag, Long>, BookmarkTagRepositoryCustom {
    List<BookmarkTag> findByBookmark(Bookmark bookmark);

    void deleteByBookmark(Bookmark bookmark);

    void deleteByBookmarkAndTag(Bookmark bookmark, Tag tag);
}
