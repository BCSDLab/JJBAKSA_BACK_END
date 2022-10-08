package com.jjbacsa.jjbacsabackend.follow.repository.dsl;

import com.jjbacsa.jjbacsabackend.follow.entity.FollowEntity;
import com.jjbacsa.jjbacsabackend.follow.entity.QFollowEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpressions;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

public class DslFollowRepositoryImpl extends QuerydslRepositorySupport implements DslFollowRepository {

    private static final QFollowEntity f = QFollowEntity.followEntity;

    public DslFollowRepositoryImpl() {
        super(FollowEntity.class);
    }

    @Override
    public Page<FollowEntity> findAllByUserWithCursor(UserEntity user, String cursor, Pageable pageable) {

        List<FollowEntity> content = from(f).select(f)
                .join(f.follower).fetchJoin()
                .where(f.user.eq(user), customCursor(cursor))
                .orderBy(f.follower.nickname.asc(), f.follower.id.asc())
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<FollowEntity> countQuery = from(f).select(f)
                .where(f.user.eq(user));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    @Override
    public Long deleteFollowWithUser(UserEntity user){
        return update(f).set(f.isDeleted, 1)
                .where(f.follower.eq(user).or(f.user.eq(user)))
                .execute();
    }

    private BooleanExpression customCursor(String cursor) {

        if (cursor == null)
            return null;

        return StringExpressions.lpad(f.follower.nickname.stringValue(), 20, '0')
                .concat(StringExpressions.lpad(f.follower.id.stringValue(), 10, '0'))
                .gt(cursor);
    }
}
