package com.jjbacsa.jjbacsabackend.post.repository.querydsl;

import com.jjbacsa.jjbacsabackend.etc.enums.BoardType;
import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import com.jjbacsa.jjbacsabackend.post.entity.QPostEntity;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

public class DslPostRepositoryImpl extends QuerydslRepositorySupport implements DslPostRepository {
    private final JPAQueryFactory queryFactory;
    private static QPostEntity post = QPostEntity.postEntity;

    public DslPostRepositoryImpl(JPAQueryFactory queryFactory){
        super(PostEntity.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<PostEntity> findAllPosts(String boardType, Pageable pageable) {

        List<PostEntity> postEntities = queryFactory
                .selectFrom(post)
                .where(post.boardType.eq(BoardType.valueOf(boardType)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(post.boardType.eq(BoardType.FAQ));

        return PageableExecutionUtils.getPage(postEntities, pageable, countQuery::fetchOne);
    }

}
