package com.jjbacsa.jjbacsabackend.inquiry.repository.querydsl;

import com.jjbacsa.jjbacsabackend.inquiry.dto.request.InquiryCursorRequest;
import com.jjbacsa.jjbacsabackend.inquiry.entity.InquiryEntity;
import com.jjbacsa.jjbacsabackend.inquiry.entity.QInquiryEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class DslInquiryRepositoryImpl extends QuerydslRepositorySupport implements DslInquiryRepository {
    private final JPAQueryFactory queryFactory;
    private static QInquiryEntity inquiry = QInquiryEntity.inquiryEntity;

    public DslInquiryRepositoryImpl(JPAQueryFactory queryFactory) {
        super(InquiryEntity.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<InquiryEntity> findAllInquiries(String dateCursor, Long idCursor, Pageable pageable) {

        List<InquiryEntity> inquiryEntities = queryFactory
                .selectFrom(inquiry)
                .where(customCursor(dateCursor, idCursor))
                .limit(pageable.getPageSize())
                .orderBy(inquiry.createdAt.desc(), inquiry.id.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(inquiry.count())
                .from(inquiry);

        return PageableExecutionUtils.getPage(inquiryEntities, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<InquiryEntity> findAllMyInquiries(String dateCursor, Long idCursor, Long userId, Pageable pageable) {

        List<InquiryEntity> inquiryEntities = queryFactory
                .selectFrom(inquiry)
                .where(inquiry.writer.id.eq(userId),
                        customCursor(dateCursor, idCursor))
                .limit(pageable.getPageSize())
                .orderBy(inquiry.createdAt.desc(), inquiry.id.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(inquiry.count())
                .where(inquiry.writer.id.eq(userId))
                .from(inquiry);

        return PageableExecutionUtils.getPage(inquiryEntities, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<InquiryEntity> findAllSearchInquiries(String dateCursor, Long idCursor, String searchWord, Pageable pageable) {
        List<InquiryEntity> inquiryEntities = queryFactory
                .selectFrom(inquiry)
                .where(inquiry.title.contains(searchWord),
                        customCursor(dateCursor, idCursor))
                .limit(pageable.getPageSize())
                .orderBy(inquiry.createdAt.desc(), inquiry.id.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(inquiry.count())
                .where(inquiry.title.contains(searchWord))
                .from(inquiry);

        return PageableExecutionUtils.getPage(inquiryEntities, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<InquiryEntity> findAllSearchMyInquiries(String dateCursor, Long idCursor, String searchWord, Long userId, Pageable pageable) {
        List<InquiryEntity> inquiryEntities = queryFactory
                .selectFrom(inquiry)
                .where(inquiry.writer.id.eq(userId),
                        inquiry.title.contains(searchWord),
                        customCursor(dateCursor, idCursor))
                .limit(pageable.getPageSize())
                .orderBy(inquiry.createdAt.desc(), inquiry.id.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(inquiry.count())
                .where(inquiry.writer.id.eq(userId),
                        inquiry.title.contains(searchWord))
                .from(inquiry);

        return PageableExecutionUtils.getPage(inquiryEntities, pageable, countQuery::fetchOne);
    }

    private BooleanExpression customCursor(String dateCursor, Long idCursor) {
        if(dateCursor == null || idCursor == null) return null;

        StringBuilder sb = new StringBuilder(dateCursor);
        sb.append(String.format("%1$" + 5 + "s", idCursor).replace(' ', '0'));

        return inquiry.createdAt.stringValue().substring(0, 10)
                .concat(StringExpressions.lpad(inquiry.id.stringValue(), 5, '0'))
                .lt(sb.toString());
    }
}
