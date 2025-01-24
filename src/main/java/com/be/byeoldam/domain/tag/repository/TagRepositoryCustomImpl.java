package com.be.byeoldam.domain.tag.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.be.byeoldam.domain.tag.model.QTag.tag;


@Repository
public class TagRepositoryCustomImpl implements TagRepositoryCustom {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Transactional
    @Override
    public void decrementReferenceCountByName(String name) {
        queryFactory.update(tag)  // 업데이트 대상은 Tag 엔티티
                .set(tag.referenceCount, tag.referenceCount.subtract(1))  // referenceCount 1 감소
                .where(tag.name.eq(name))  // 조건: name을 기준으로
                .execute();  // 쿼리 실행
    }

}
