package com.jjbacsa.jjbacsabackend.user.repository.querydsl;

import com.jjbacsa.jjbacsabackend.user.entity.QUserEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

public class DslUserRepositoryImpl extends QuerydslRepositorySupport implements DslUserRepository {
    private static final QUserEntity f = QUserEntity.userEntity;

    public DslUserRepositoryImpl(){ super(UserEntity.class); }

    @Override
    public Page<UserEntity> findAllByUserByNameWithCursor(String keyword, Pageable pageable, Long cursor){
        List<UserEntity> users = from(f).select(f)
                .join(f.userCount).fetchJoin()
                .where(f.nickname.contains(keyword), f.id.gt(cursor == null ? 0 : cursor))
                .orderBy(new CaseBuilder()
                        .when(f.nickname.eq(keyword)).then(0)
                        .when(f.nickname.like(keyword + "%")).then(1)
                        .when(f.nickname.like("%" + keyword + "%")).then(2)
                        .otherwise(3).asc(), f.id.asc())
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<UserEntity> countQuery = from(f).select(f)
                .where(f.nickname.contains(keyword));

        return PageableExecutionUtils.getPage(users, pageable, countQuery::fetchCount);
    }
}
