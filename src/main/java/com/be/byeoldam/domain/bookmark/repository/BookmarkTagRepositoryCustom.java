package com.be.byeoldam.domain.bookmark.repository;

import com.be.byeoldam.domain.bookmark.model.Bookmark;

import java.util.List;

public interface BookmarkTagRepositoryCustom {
    List<Bookmark> findBookmarksByUserIdAndTagNameWithPaging(Long userId, String tagName, int offset, int limit);
}
