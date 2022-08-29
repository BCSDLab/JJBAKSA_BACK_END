package com.jjbacsa.jjbacsabackend.review.repository.querydsl;

import com.jjbacsa.jjbacsabackend.image.entity.QImageEntity;
import com.jjbacsa.jjbacsabackend.review.entity.QReviewEntity;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review_image.entity.QReviewImageEntity;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import com.jjbacsa.jjbacsabackend.shop.entity.QShopEntity;
import com.jjbacsa.jjbacsabackend.user.entity.QUserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class DslReviewRepositoryImpl extends QuerydslRepositorySupport implements DslReviewRepository {
    private final JPAQueryFactory queryFactory;

    public DslReviewRepositoryImpl(JPAQueryFactory queryFactory){
        super(ReviewEntity.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public ReviewEntity findByReviewId(Long reviewId) {
        QReviewEntity review = QReviewEntity.reviewEntity;
        QUserEntity user = QUserEntity.userEntity;
        QShopEntity shop = QShopEntity.shopEntity;
        QReviewImageEntity reviewImageEntity = QReviewImageEntity.reviewImageEntity;
        QImageEntity image = QImageEntity.imageEntity;

        // ToOne 관계 페치 조인
        ReviewEntity reviewEntity = queryFactory
                .selectFrom(review)
                .innerJoin(review.writer, user).fetchJoin()
                .innerJoin(review.shop, shop).fetchJoin()
                .where(review.id.eq(reviewId))
                .fetchOne();
        // ToMany 관계 컬렉션에 대한 ToOne 페치 조인 -> 리뷰 이미지는 없을 수 있기 때문에 한번에 Review와 조회하면 null이 되기에 분리하여 조회
        List<ReviewImageEntity> reviewImages = queryFactory
                .selectFrom(reviewImageEntity)
                .innerJoin(reviewImageEntity.image, image).fetchJoin()
                .where(reviewImageEntity.review.id.eq(reviewId))
                .fetch();
        return reviewEntity;
    }

}
