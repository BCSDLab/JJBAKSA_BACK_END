package com.jjbacsa.jjbacsabackend.user.repository.querydsl;

import com.jjbacsa.jjbacsabackend.follow.entity.QFollowEntity;
import com.jjbacsa.jjbacsabackend.user.entity.QUserCount;
import com.jjbacsa.jjbacsabackend.user.entity.UserCount;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.querydsl.jpa.JPAExpressions;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class DslUserCountRepositoryImpl extends QuerydslRepositorySupport implements  DslUserCountRepository{
    private static final QUserCount qUserCount = QUserCount.userCount;

    public DslUserCountRepositoryImpl(){ super(UserCount.class); }

    @Override
    public Long updateAllFriendsCountByUser(UserEntity user){
        QFollowEntity follow = QFollowEntity.followEntity;

        return update(qUserCount)
                .set(qUserCount.friendCount,
                        qUserCount.friendCount.subtract(1))
                .where(qUserCount.user.id.in(
                        JPAExpressions
                                .select(follow.user.id)
                                .from(follow)
                                .where(follow.follower.eq(user))
                )).execute();
    }
}
