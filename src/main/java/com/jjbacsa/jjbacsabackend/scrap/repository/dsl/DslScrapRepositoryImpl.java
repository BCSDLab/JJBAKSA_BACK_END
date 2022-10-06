package com.jjbacsa.jjbacsabackend.scrap.repository.dsl;

import com.jjbacsa.jjbacsabackend.scrap.entity.QScrapEntity;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class DslScrapRepositoryImpl extends QuerydslRepositorySupport implements DslScrapRepository {

    private static final QScrapEntity s = QScrapEntity.scrapEntity;

    @PersistenceContext
    private EntityManager em;

    public DslScrapRepositoryImpl() {
        super(ScrapEntity.class);
    }


    @Override
    public Page<ScrapEntity> findAllByUserAndDirectoryWithCursor(UserEntity user, ScrapDirectoryEntity directory, Long cursor, Pageable pageable) {

        BooleanExpression condition = getScrapCondition(user, directory);

        List<ScrapEntity> content = from(s).select(s)
                .where(condition, customCursor(cursor))
                .orderBy(s.id.asc())
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<ScrapEntity> countQuery = from(s).select(s)
                .where(condition);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    @Override
    public long deleteAllByDirectory(ScrapDirectoryEntity directory) {

        long deletedCount = update(s)
                .set(s.isDeleted, 1)
                .where(s.directory.eq(directory))
                .execute();
        em.clear();

        return deletedCount;
    }

    private BooleanExpression getScrapCondition(UserEntity user, ScrapDirectoryEntity directory) {

        if (directory == null)
            return s.user.eq(user).and(s.directory.isNull());

        return s.user.eq(user).and(s.directory.eq(directory));
    }

    private BooleanExpression customCursor(Long cursor) {

        if (cursor == null)
            return null;

        return s.id.gt(cursor);
    }
}
