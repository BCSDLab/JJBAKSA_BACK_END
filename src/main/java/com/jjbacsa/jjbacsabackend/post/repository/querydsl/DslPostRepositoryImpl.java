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

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;

public class DslPostRepositoryImpl extends QuerydslRepositorySupport implements DslPostRepository {
    private final JPAQueryFactory queryFactory;
    private static QPostEntity post = QPostEntity.postEntity;

    public DslPostRepositoryImpl(JPAQueryFactory queryFactory){
        super(PostEntity.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<PostEntity> findAllNotices(Pageable pageable) {
        OrderSpecifier<?>[] order = new OrderSpecifier[] {
                orderByEnum(BoardType.POWER_NOTICE),
                post.createdAt.desc(),
                post.id.desc()
        };
        List<PostEntity> postEntities = queryFactory
                .selectFrom(post)
                .where(post.boardType.in(BoardType.POWER_NOTICE, BoardType.NOTICE))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order)
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(post.boardType.in(BoardType.POWER_NOTICE, BoardType.NOTICE));

        return PageableExecutionUtils.getPage(postEntities, pageable, countQuery::fetchOne);
    }

    // Todo: INQUIRY 기능 구현 후 구현
    @Override
    public Page<PostEntity> findAllInquiries(Pageable pageable) {
        return Page.empty();
    }

    private OrderSpecifier<Integer> orderByEnum(BoardType boardType) {
        NumberExpression<Integer> cases = new CaseBuilder()
                .when(post.boardType.eq(boardType))
                        .then(1)
                .otherwise(2);
        return new OrderSpecifier<>(Order.ASC, cases);
    }

}
