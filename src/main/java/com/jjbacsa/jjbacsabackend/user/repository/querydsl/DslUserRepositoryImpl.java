package com.jjbacsa.jjbacsabackend.user.repository.querydsl;

import com.jjbacsa.jjbacsabackend.etc.enums.FollowedType;
import com.jjbacsa.jjbacsabackend.follow.entity.QFollowEntity;
import com.jjbacsa.jjbacsabackend.follow.entity.QFollowRequestEntity;
import com.jjbacsa.jjbacsabackend.image.entity.QImageEntity;
import com.jjbacsa.jjbacsabackend.user.entity.QUserCount;
import com.jjbacsa.jjbacsabackend.user.entity.QUserEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringExpressions;
import com.querydsl.jpa.JPQLQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

public class DslUserRepositoryImpl extends QuerydslRepositorySupport implements DslUserRepository {

    private static final QUserEntity qUser = QUserEntity.userEntity;
    private static final QImageEntity qImage = QImageEntity.imageEntity;
    private static final QFollowEntity qFollow = QFollowEntity.followEntity;
    private static final QFollowRequestEntity qFollowRequest = QFollowRequestEntity.followRequestEntity;

    public DslUserRepositoryImpl() {
        super(UserEntity.class);
    }

    @Override
    public Page<UserEntity> findAllByUserNameWithCursor(String keyword, Pageable pageable, Long cursor) {

        String cursorNickname = null;
        if (cursor != null) {
            cursorNickname = from(qUser).select(qUser.nickname).where(qUser.id.eq(cursor)).fetchOne();
        }

        List<UserEntity> users = from(qUser).select(qUser)
                .join(qUser.userCount).fetchJoin()
                .leftJoin(qImage).on(qUser.profileImage.eq(qImage))
                .where(qUser.nickname.contains(keyword))
                .where(getCursorExpression(cursor, cursorNickname, keyword))
                .orderBy(new OrderSpecifier(Order.ASC, createCursorExpression(keyword)))
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<UserEntity> countQuery = from(qUser).select(qUser)
                .where(qUser.nickname.contains(keyword));

        return PageableExecutionUtils.getPage(users, pageable, countQuery::fetchCount);
    }

    private BooleanExpression getCursorExpression(Long cursor, String nickname, String keyword) {

        if (cursor == null) {
            return null;
        }

        Integer equalState = 3;

        if (nickname.equals(keyword)) {
            equalState = 1;
        } else if (nickname.startsWith(keyword)) {
            equalState = 2;
        }

        StringExpression cursorExpression = Expressions.asString(equalState.toString()).concat(
                StringExpressions.lpad(Expressions.asString(cursor.toString()), 10, '0'));

        return cursorExpression.lt(createCursorExpression(keyword));
    }

    private StringExpression createCursorExpression(String keyword) {

        return new CaseBuilder()
                .when(qUser.nickname.eq(keyword)).then(1)
                .when(qUser.nickname.like(keyword + "%")).then(2)
                .otherwise(3).stringValue()
                .concat(StringExpressions.lpad(qUser.id.stringValue(), 10, '0'));
    }

    @Override
    public Map<Long, FollowedType> getFollowedTypesByUserAndUsers(UserEntity user, List<UserEntity> users) {

        Map<Long, FollowedType> map = new HashMap<>();

        // Follower check
        List<Tuple> tuples = from(qUser)
                .select(qUser.id, qFollow.isNotNull())
                .leftJoin(qFollow).on(qFollow.user.eq(user), qFollow.follower.eq(qUser), qFollow.isDeleted.eq(0))
                .where(qUser.in(users))
                .fetch();
        map = getFollowedType(map, tuples, FollowedType.FOLLOWED);

        // Follow request check
        tuples = from(qUser)
                .select(qUser.id, qFollowRequest.isNotNull())
                .leftJoin(qFollowRequest).on(qFollowRequest.user.eq(user), qFollowRequest.follower.eq(qUser), qFollowRequest.isDeleted.eq(0))
                .where(qUser.in(users))
                .fetch();
        map = getFollowedType(map, tuples, FollowedType.REQUEST_SENT);

        // Follow request receive check
        tuples = from(qUser)
                .select(qUser.id, qFollowRequest.isNotNull())
                .leftJoin(qFollowRequest).on(qFollowRequest.follower.eq(user), qFollowRequest.user.eq(qUser), qFollowRequest.isDeleted.eq(0))
                .where(qUser.in(users))
                .fetch();
        map = getFollowedType(map, tuples, FollowedType.REQUEST_RECEIVED);

        return map;
    }

    private Long getId(Tuple tuple) {
        return tuple.get(0, Long.class);
    }

    private Map<Long, FollowedType> getFollowedType(Map<Long, FollowedType> map, List<Tuple> tuples, FollowedType type) {
        for(Tuple tuple : tuples) {
            Long userId = getId(tuple);
            if (itemIsTrue(tuple, 1)) {
                map.put(userId, type);
            } else if (!map.containsKey(userId)) map.put(userId, FollowedType.NONE);
        }
        return map;
    }

    private boolean itemIsTrue(Tuple tuple, int index) {
        return Objects.equals(Boolean.TRUE, tuple.get(index, Boolean.class));
    }

    @Override
    public UserEntity findUserByIdWithCount(Long id) {
        return from(qUser).select(qUser)
                .join(qUser.userCount).fetchJoin()
                .leftJoin(qImage).on(qUser.profileImage.eq(qImage))
                .where(qUser.id.eq(id))
                .fetchOne();
    }

    @Override
    public List<UserEntity> findAllUserByIdAndFollowWithCount(Long id) {
        QFollowEntity follow = QFollowEntity.followEntity;
        QUserCount userCount = QUserCount.userCount;

        return from(qUser).select(qUser)
                .join(follow).on(qUser.eq(follow.follower)).fetchJoin()
                .join(userCount).on(qUser.eq(userCount.user)).fetchJoin()
                .where(qUser.id.eq(id))
                .fetch();
    }
}
