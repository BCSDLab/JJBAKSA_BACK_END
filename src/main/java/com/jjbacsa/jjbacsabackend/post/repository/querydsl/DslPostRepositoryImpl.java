package com.jjbacsa.jjbacsabackend.post.repository.querydsl;


import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import com.jjbacsa.jjbacsabackend.post.entity.QPostEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class DslPostRepositoryImpl extends QuerydslRepositorySupport implements DslPostRepository {
    private final JPAQueryFactory queryFactory;
    private static QPostEntity post = QPostEntity.postEntity;

    public DslPostRepositoryImpl(JPAQueryFactory queryFactory) {
        super(PostEntity.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<PostEntity> findAllPosts(String dateCursor, Long idCursor, Pageable pageable) {

        List<PostEntity> postEntities = queryFactory
                .selectFrom(post)
                .where(customCursor(dateCursor, idCursor))
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc(), post.id.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post);

        return PageableExecutionUtils.getPage(postEntities, pageable, countQuery::fetchOne);
    }

    private BooleanExpression customCursor(String dateCursor, Long idCursor) {
        log.info(dateCursor + idCursor);
        if(dateCursor == null || idCursor == null) return null;

        StringBuilder sb = new StringBuilder(dateCursor);
        sb.append(String.format("%1$" + 5 + "s", idCursor).replace(' ', '0'));

        return post.createdAt.stringValue().substring(0, 10)
                .concat(StringExpressions.lpad(post.id.stringValue(), 5, '0'))
                        .lt(sb.toString());
    }

}
