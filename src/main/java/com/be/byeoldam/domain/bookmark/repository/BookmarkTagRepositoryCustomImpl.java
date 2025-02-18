package com.be.byeoldam.domain.bookmark.repository;

import com.be.byeoldam.domain.bookmark.model.Bookmark;
import com.be.byeoldam.domain.bookmark.model.QBookmark;
import com.be.byeoldam.domain.bookmark.model.QBookmarkTag;
import com.be.byeoldam.domain.tag.model.QTag;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public class BookmarkTagRepositoryCustomImpl implements BookmarkTagRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Bookmark> findBookmarksByUserIdAndTagNameWithPaging(Long userId, String tagName, int offset, int limit) {
        QBookmark bookmark = QBookmark.bookmark;
        QBookmarkTag bookmarkTag = QBookmarkTag.bookmarkTag;
        QTag tag = QTag.tag;

        return queryFactory
                .select(bookmark)
                .distinct() // 중복된 Bookmark 제거
                .from(bookmarkTag)
                .join(bookmarkTag.bookmark, bookmark) // BookmarkTag → Bookmark 조인
                .join(bookmarkTag.tag, tag) // BookmarkTag → Tag 조인
                .where(
                        bookmark.user.id.eq(userId), // 유저 필터링
                        tag.name.eq(tagName) // 태그 이름 필터링
                )
                .offset(offset) // 페이징 시작 위치
                .limit(limit)   // 가져올 데이터 개수
                .fetch();
    }
}
