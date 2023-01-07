package com.jjbacsa.jjbacsabackend.review.repository.querydsl;

import com.jjbacsa.jjbacsabackend.follow.entity.FollowEntity;
import com.jjbacsa.jjbacsabackend.follow.entity.QFollowEntity;
import com.jjbacsa.jjbacsabackend.image.entity.QImageEntity;
import com.jjbacsa.jjbacsabackend.review.entity.QReviewEntity;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review_image.entity.QReviewImageEntity;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import com.jjbacsa.jjbacsabackend.shop.entity.QShopCount;
import com.jjbacsa.jjbacsabackend.shop.entity.QShopEntity;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.user.entity.QUserEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class DslReviewRepositoryImpl extends QuerydslRepositorySupport implements DslReviewRepository {
    private final JPAQueryFactory queryFactory;
    private static QReviewEntity review = QReviewEntity.reviewEntity;
    private static QUserEntity user = QUserEntity.userEntity;
    private static QShopEntity shop = QShopEntity.shopEntity;
    private static QReviewImageEntity reviewImageEntity = QReviewImageEntity.reviewImageEntity;
    private static QImageEntity image = QImageEntity.imageEntity;
    private static QFollowEntity follow = QFollowEntity.followEntity;
    private static QShopCount shopCount = QShopCount.shopCount;

    public DslReviewRepositoryImpl(JPAQueryFactory queryFactory){
        super(ReviewEntity.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public ReviewEntity findByReviewId(Long reviewId) {
        // ToOne 관계 페치 조인
        ReviewEntity reviewEntity = queryFactory
                .selectFrom(review)
                .innerJoin(review.writer, user).fetchJoin()
                .innerJoin(review.shop, shop).fetchJoin()
                .where(review.id.eq(reviewId))
                .fetchOne();
        // ToMany 관계 컬렉션에 대한 ToOne 페치 조인 -> 리뷰 이미지는 없을 수 있기 때문에 한번에 Review와 조회하면 null이 되기에 분리하여 조회
        List<ReviewImageEntity> reviewImages = findAllReviewImages(reviewId);
        return reviewEntity;
    }

    @Override
    public Page<ReviewEntity> findAllByFollowerId(Long followerId, Pageable pageable) {
        List<ReviewEntity> reviewEntities = queryFactory
                .selectFrom(review)
                .innerJoin(review.writer, user).fetchJoin()
                .innerJoin(review.shop, shop).fetchJoin()
                .where(review.writer.id.eq(followerId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(review.createdAt.desc())
                .fetch();
        for(ReviewEntity reviewEntity: reviewEntities){
            if(reviewEntity.getReviewImages() != null) {
                List<ReviewImageEntity> reviewImages = findAllReviewImages(reviewEntity.getId());
            }
        }
        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(review.writer.id.eq(followerId));
        return PageableExecutionUtils.getPage(reviewEntities, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<ReviewEntity> findAllFriendsReview(Long userId, Pageable pageable) {

        List<Long> followerId = findAllFollowersId(userId);

        // 팔로워들이 작성한 리뷰
        List<ReviewEntity> reviewEntities = queryFactory
                .selectFrom(review)
                .innerJoin(review.writer, user).fetchJoin()
                .innerJoin(review.shop, shop).fetchJoin()
                .where(review.writer.id.in(followerId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(review.createdAt.desc())
                .fetch();
        for(ReviewEntity reviewEntity: reviewEntities){
            if(reviewEntity.getReviewImages() != null) {
                List<ReviewImageEntity> reviewImages = findAllReviewImages(reviewEntity.getId());
            }
        }
        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(review.writer.id.in(followerId));
        return PageableExecutionUtils.getPage(reviewEntities, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<ReviewEntity> findAllFollowersReviewsByShopId(Long userId, Long shopId, Pageable pageable) {
        List<Long> followerId = findAllFollowersId(userId);

        // 팔로워들이 shop에 대해 작성한 리뷰
        List<ReviewEntity> reviewEntities = queryFactory
                .selectFrom(review)
                .innerJoin(review.writer, user).fetchJoin()
                .innerJoin(review.shop, shop).fetchJoin()
                .where(review.writer.id.in(followerId), review.shop.id.eq(shopId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(review.createdAt.desc())
                .fetch();

        for(ReviewEntity reviewEntity: reviewEntities){
            if(reviewEntity.getReviewImages() != null) {
                List<ReviewImageEntity> reviewImages = findAllReviewImages(reviewEntity.getId());
            }
        }
        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(review.writer.id.in(followerId), review.shop.id.eq(shopId));
        return PageableExecutionUtils.getPage(reviewEntities, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<ShopEntity> findAllShopBySearchWordInReviewWithCursor(String cursor, Long userId, String searchWord, Pageable pageable) {
        List<Long> followerId = findAllFollowersId(userId);

        // 팔로워들이 작성한 리뷰에 searchWord가 포함된 상점 List
        List<ShopEntity> reviewShopEntities = queryFactory
                .selectFrom(shop)
                .where(shop.id.in(
                        JPAExpressions.select(review.shop.id).from(review)
                        .where(review.writer.id.in(followerId), review.content.contains(searchWord))
                        .distinct()
                        ), customCursor(cursor))
                .innerJoin(shop.shopCount, shopCount).fetchJoin()
                .limit(pageable.getPageSize())
                .orderBy(shop.placeName.asc(), shop.id.asc())
                .fetch();
        JPAQuery<Long> countQuery = queryFactory
                .select(shop.count())
                .from(shop);

        return PageableExecutionUtils.getPage(reviewShopEntities, pageable, countQuery::fetchOne);
    }

    private List<Long> findAllFollowersId(Long userId){
        List<FollowEntity> followers = queryFactory
                .selectFrom(follow)
                .innerJoin(follow.user, user).fetchJoin()
                .innerJoin(follow.follower, user).fetchJoin()
                .where(follow.user.id.eq(userId))
                .fetch();

        return followers.stream().map(o -> o.getFollower().getId()).collect(Collectors.toList());
    }
    private List<ReviewImageEntity> findAllReviewImages(Long reviewId){
        return queryFactory
                .selectFrom(reviewImageEntity)
                .innerJoin(reviewImageEntity.image, image).fetchJoin()
                .where(reviewImageEntity.review.id.eq(reviewId))
                .fetch();
    }
    private BooleanExpression customCursor(String cursor){
        if(cursor==null) return null;

        return StringExpressions.lpad(shop.placeName.stringValue(), 20, '0')
                .concat(StringExpressions.lpad(shop.id.stringValue(), 10, '0'))
                .gt(cursor);

    }
}
