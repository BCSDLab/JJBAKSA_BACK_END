package com.jjbacsa.jjbacsabackend.scrap.repository;

import com.jjbacsa.jjbacsabackend.scrap.entity.QScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpressions;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

public class DslScrapDirectoryRepositoryImpl extends QuerydslRepositorySupport implements DslScrapDirectoryRepository {

    private static final QScrapDirectoryEntity d = QScrapDirectoryEntity.scrapDirectoryEntity;

    public DslScrapDirectoryRepositoryImpl() {
        super(ScrapDirectoryEntity.class);
    }

    @Override
    public Page<ScrapDirectoryEntity> findAllByUserWithCursor(UserEntity user, String cursor, Pageable pageable) {

        List<ScrapDirectoryEntity> content = from(d).select(d)
                .join(d.scrapDirectoryCount).fetchJoin()
                .where(d.user.eq(user), customCursor(cursor))
                .orderBy(d.name.asc(), d.id.asc())
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<ScrapDirectoryEntity> countQuery = from(d).select(d)
                .where(d.user.eq(user));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    private BooleanExpression customCursor(String cursor) {

        if (cursor == null)
            return null;

        return StringExpressions.lpad(d.name.stringValue(), 10, '0')
                .concat(StringExpressions.lpad(d.id.stringValue(), 10, '0'))
                .gt(cursor);
    }
}
