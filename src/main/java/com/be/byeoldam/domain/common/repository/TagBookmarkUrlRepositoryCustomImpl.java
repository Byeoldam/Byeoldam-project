package com.be.byeoldam.domain.common.repository;

import com.be.byeoldam.domain.common.model.QBookmarkUrl;
import com.be.byeoldam.domain.common.model.QTagBookmarkUrl;
import com.be.byeoldam.domain.tag.dto.RecommendedUrlResponse;
import com.be.byeoldam.domain.tag.model.QTag;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;



@RequiredArgsConstructor
public class TagBookmarkUrlRepositoryCustomImpl implements TagBookmarkUrlRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<RecommendedUrlResponse> findBookmarkUrlsByTagName(String tagName, int offset, int size) {
        QTagBookmarkUrl tagBookmarkUrl = QTagBookmarkUrl.tagBookmarkUrl;
        QTag tag = QTag.tag;
        QBookmarkUrl bookmarkUrl = QBookmarkUrl.bookmarkUrl;

        return queryFactory
                .select(Projections.constructor(RecommendedUrlResponse.class,
                        bookmarkUrl.url,
                        bookmarkUrl.readingTime,
                        bookmarkUrl.description,
                        bookmarkUrl.img,
                        bookmarkUrl.title))
                .from(tagBookmarkUrl)
                .join(tagBookmarkUrl.tag, tag)
                .join(tagBookmarkUrl.bookmarkUrl, bookmarkUrl)
                .where(tag.name.eq(tagName)) // 태그 이름이 '여행'인 경우 필터링
                .orderBy(bookmarkUrl.referenceCount.desc()) // 참조 횟수 내림차순 정렬
                .offset(offset) // OFFSET 추가
                .limit(size) // size만큼 가져오기
                .fetch();
    }

}
