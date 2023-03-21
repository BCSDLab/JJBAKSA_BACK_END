package com.jjbacsa.jjbacsabackend.user.repository.querydsl;

import com.jjbacsa.jjbacsabackend.etc.enums.FollowedType;
import com.jjbacsa.jjbacsabackend.follow.entity.QFollowEntity;
import com.jjbacsa.jjbacsabackend.follow.entity.QFollowRequestEntity;
import com.jjbacsa.jjbacsabackend.image.entity.QImageEntity;
import com.jjbacsa.jjbacsabackend.user.entity.QUserCount;
import com.jjbacsa.jjbacsabackend.user.entity.QUserEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPQLQuery;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

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
        List<UserEntity> users = from(qUser).select(qUser)
                .join(qUser.userCount).fetchJoin()
                .leftJoin(qImage).on(qUser.profileImage.eq(qImage))
                .where(qUser.nickname.contains(keyword).or(qUser.account.contains(keyword)))
                .where(qUser.id.gt(cursor == null ? 0 : cursor))
                .orderBy(new CaseBuilder()
                        .when(qUser.nickname.eq(keyword)
                                .or(qUser.account.eq(keyword))).then(0)
                        .when(qUser.nickname.like(keyword + "%")
                                .or(qUser.account.like(keyword + "%"))).then(1)
                        .otherwise(2).asc(), qUser.id.asc())
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<UserEntity> countQuery = from(qUser).select(qUser)
                .where(qUser.nickname.contains(keyword));

        return PageableExecutionUtils.getPage(users, pageable, countQuery::fetchCount);
    }

    @Override
    public Map<Long, FollowedType> getFollowedTypesByUserAndUsers(UserEntity user, List<UserEntity> users) {
        return from(qUser)
                .select(qUser.id, qFollow.isNotNull(), qFollowRequest.isNotNull())
                .leftJoin(qFollow).on(qFollow.user.eq(user), qFollow.follower.eq(qUser))
                .leftJoin(qFollowRequest).on(qFollowRequest.user.eq(user), qFollowRequest.follower.eq(qUser))
                .where(qUser.in(users))
                .fetch()
                .stream()
                .collect(Collectors.toMap(this::getId, this::getFollowedType));
    }

    private Long getId(Tuple tuple) {
        return tuple.get(0, Long.class);
    }

    private FollowedType getFollowedType(Tuple tuple) {
        if (itemIsTrue(tuple, 1)) {
            return FollowedType.FOLLOWED;
        }
        if (itemIsTrue(tuple, 2)) {
            return FollowedType.REQUESTED;
        }
        return FollowedType.NONE;
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
