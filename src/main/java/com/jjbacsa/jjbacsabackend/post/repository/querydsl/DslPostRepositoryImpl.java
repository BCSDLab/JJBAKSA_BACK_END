package com.jjbacsa.jjbacsabackend.post.repository.querydsl;

import com.jjbacsa.jjbacsabackend.etc.enums.BoardType;
import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import com.jjbacsa.jjbacsabackend.post.entity.QPostEntity;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;

public class DslPostRepositoryImpl extends QuerydslRepositorySupport implements DslPostRepository {
    private final JPAQueryFactory queryFactory;
    private static QPostEntity post = QPostEntity.postEntity;

    public DslPostRepositoryImpl(JPAQueryFactory queryFactory) {
        super(PostEntity.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<PostEntity> findAllNotices(String cursor, String boardType, Pageable pageable) {

        List<PostEntity> postEntities = queryFactory
                .selectFrom(post)
                .where(post.createdAt.stringValue().lt(cursor == null ? LocalDateTime.now().toString() : cursor),
                        post.boardType.stringValue().eq(boardType))
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc(), post.id.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(post.boardType.stringValue().eq(boardType));

        return PageableExecutionUtils.getPage(postEntities, pageable, countQuery::fetchOne);
    }

}
