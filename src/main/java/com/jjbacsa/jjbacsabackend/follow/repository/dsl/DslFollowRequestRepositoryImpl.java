package com.jjbacsa.jjbacsabackend.follow.repository.dsl;

import com.jjbacsa.jjbacsabackend.follow.entity.FollowRequestEntity;
import com.jjbacsa.jjbacsabackend.follow.entity.QFollowRequestEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

public class DslFollowRequestRepositoryImpl extends QuerydslRepositorySupport implements DslFollowRequestRepository {

    private static final QFollowRequestEntity fr = QFollowRequestEntity.followRequestEntity;

    public DslFollowRequestRepositoryImpl() {
        super(FollowRequestEntity.class);
    }

    @Override
    public Page<FollowRequestEntity> findAllByUser(UserEntity user, Pageable pageable) {

        return findAll(fr.user.eq(user), pageable);
    }

    @Override
    public Page<FollowRequestEntity> findAllByFollower(UserEntity follower, Pageable pageable) {

        return findAll(fr.follower.eq(follower), pageable);
    }

    private Page<FollowRequestEntity> findAll(Predicate condition, Pageable pageable) {

        List<FollowRequestEntity> content = from(fr).select(fr)
                .join(fr.follower).fetchJoin()
                .where(condition)
                .orderBy(fr.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<FollowRequestEntity> countQuery = from(fr).select(fr)
                .where(condition);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);

    }
}
