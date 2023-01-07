package com.jjbacsa.jjbacsabackend.user.repository.querydsl;

import com.jjbacsa.jjbacsabackend.follow.entity.QFollowEntity;
import com.jjbacsa.jjbacsabackend.image.entity.QImageEntity;
import com.jjbacsa.jjbacsabackend.user.entity.QUserCount;
import com.jjbacsa.jjbacsabackend.user.entity.QUserEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

public class DslUserRepositoryImpl extends QuerydslRepositorySupport implements DslUserRepository {
    private static final QUserEntity qUser = QUserEntity.userEntity;
    private static final QImageEntity qImage = QImageEntity.imageEntity;

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
