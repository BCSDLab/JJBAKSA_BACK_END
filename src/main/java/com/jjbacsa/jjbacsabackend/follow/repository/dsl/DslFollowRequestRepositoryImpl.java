package com.jjbacsa.jjbacsabackend.follow.repository.dsl;

import com.jjbacsa.jjbacsabackend.follow.entity.FollowRequestEntity;
import com.jjbacsa.jjbacsabackend.follow.entity.QFollowRequestEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

public class DslFollowRequestRepositoryImpl extends QuerydslRepositorySupport implements DslFollowRequestRepository {

    private static final String ZONE = "Asia/Seoul";
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

    @Override
    public Long deleteFollowRequestWithUser(UserEntity user) {
        return update(fr).set(fr.isDeleted, 1)
                .where(fr.user.id.eq(user.getId()).or(fr.follower.id.eq(user.getId())))
                .execute();
    }

    @Override
    public Boolean existsFollowReqeustsInLast24Hours(Long userId) {
        Date oneDayAgo = convertLocalDateTimeToDate(LocalDateTime.now().minusDays(1));

        return from(fr).select(fr)
                .where(fr.follower.id.eq(userId), fr.createdAt.after(oneDayAgo))
                .fetchFirst() == null ? false : true;
    }

    private Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.of(ZONE)).toInstant());
    }
}
