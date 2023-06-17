package com.jjbacsa.jjbacsabackend.review.repository.querydsl;

import com.jjbacsa.jjbacsabackend.follow.entity.FollowEntity;
import com.jjbacsa.jjbacsabackend.follow.entity.QFollowEntity;
import com.jjbacsa.jjbacsabackend.google.entity.QGoogleShopCount;
import com.jjbacsa.jjbacsabackend.google.entity.QGoogleShopEntity;
import com.jjbacsa.jjbacsabackend.image.entity.QImageEntity;
import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewCursorRequest;
import com.jjbacsa.jjbacsabackend.review.entity.QReviewEntity;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review_image.entity.QReviewImageEntity;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import com.jjbacsa.jjbacsabackend.user.entity.QUserEntity;
import com.jjbacsa.jjbacsabackend.util.QueryDslUtil;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import com.querydsl.core.types.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class DslReviewRepositoryImpl extends QuerydslRepositorySupport implements DslReviewRepository {
    private final JPAQueryFactory queryFactory;

    private static QReviewEntity review = QReviewEntity.reviewEntity;
    private static QUserEntity user = QUserEntity.userEntity;
    private static QGoogleShopEntity shop = QGoogleShopEntity.googleShopEntity;
    private static QReviewImageEntity reviewImageEntity = QReviewImageEntity.reviewImageEntity;
    private static QImageEntity image = QImageEntity.imageEntity;
    private static QFollowEntity follow = QFollowEntity.followEntity;
    private static QGoogleShopCount shopCount = QGoogleShopCount.googleShopCount;

    public DslReviewRepositoryImpl(JPAQueryFactory queryFactory) {
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
    public Long getReviewCount(Long userId) {
        return queryFactory.selectFrom(review)
                .select(review.count())
                .where(review.writer.id.eq(userId))
                .fetchOne();
    }

    @Override
    public Long getFollowersReviewCountByShop(Long userId, String placeId) {
        List<Long> followerIds = findAllFollowerIds(userId);
        return queryFactory.selectFrom(review)
                .select(review.count())
                .where(review.writer.id.in(followerIds),
                        review.shop.placeId.eq(placeId))
                .fetchOne();
    }

    @Override
    public Date getFollowersReviewLastDateByShop(Long userId, String placeId) {
        List<Long> followerIds = findAllFollowerIds(userId);
        return queryFactory.selectFrom(review)
                .select(review.createdAt)
                .where(review.writer.id.in(followerIds),
                        review.shop.placeId.eq(placeId))
                .orderBy(review.createdAt.desc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public Date getReviewLastDateByShop(Long userId, String placeId) {
        return queryFactory.selectFrom(review)
                .select(review.createdAt)
                .where(review.writer.id.eq(userId),
                        review.shop.placeId.eq(placeId))
                .orderBy(review.createdAt.desc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public Page<ReviewEntity> findAllByShopPlaceId(Long userId, String placeId, ReviewCursorRequest request) {
        PageRequest pageable = request.of();
        List<OrderSpecifier> orders = getAllOrderSpecifiers(pageable);
        List<ReviewEntity> reviews = queryFactory
                .selectFrom(review)
                .innerJoin(review.writer, user).fetchJoin()
                .innerJoin(review.shop, shop).fetchJoin()
                .where(review.writer.id.eq(userId),
                        review.shop.placeId.eq(placeId),
                        customCursor(request.getIdCursor(), request.getRateCursor(), request.getDateCursor(), request.getSort(), request.getDirection()))
                .orderBy(orders.stream().toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(review.shop.placeId.eq(placeId));
        return PageableExecutionUtils.getPage(reviews, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<ReviewEntity> findAllFollowersReviewsByShopPlaceId(Long userId, String placeId, ReviewCursorRequest request) {
        List<Long> followerIds = findAllFollowerIds(userId);
        PageRequest pageable = request.of();
        List<OrderSpecifier> orders = getAllOrderSpecifiers(pageable);

        List<ReviewEntity> reviews = queryFactory
                .selectFrom(review)
                .innerJoin(review.writer, user).fetchJoin()
                .innerJoin(review.shop, shop).fetchJoin()
                .where(review.writer.id.in(followerIds),
                        review.shop.placeId.eq(placeId),
                        customCursor(request.getIdCursor(), request.getRateCursor(), request.getDateCursor(), request.getSort(), request.getDirection())
                )
                .orderBy(orders.stream().toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(review.writer.id.in(followerIds),
                        review.shop.placeId.eq(placeId));
        return PageableExecutionUtils.getPage(reviews, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<ReviewEntity> findAllFollowerReviewsByShopPlaceId(Long followerId, String placeId, ReviewCursorRequest request) {
        PageRequest pageable = request.of();
        List<OrderSpecifier> orders = getAllOrderSpecifiers(pageable);

        List<ReviewEntity> reviews = queryFactory
                .selectFrom(review)
                .innerJoin(review.writer, user).fetchJoin()
                .innerJoin(review.shop, shop).fetchJoin()
                .where(review.writer.id.eq(followerId),
                        review.shop.placeId.eq(placeId),
                        customCursor(request.getIdCursor(), request.getRateCursor(), request.getDateCursor(), request.getSort(), request.getDirection())
                )
                .orderBy(orders.stream().toArray(OrderSpecifier[]::new))
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(review.writer.id.in(followerId),
                        review.shop.placeId.eq(placeId));
        return PageableExecutionUtils.getPage(reviews, pageable, countQuery::fetchOne);
    }

    @Override
    public List<String> findShopPlaceIdsByMyReviews(Long userId, Long cursor, Pageable pageable) throws Exception {
        List<Tuple> results = queryFactory
                .selectDistinct(review.shop.placeId, review.shop.id)
                .from(review)
                .join(review.shop, shop)
                .where(review.writer.id.eq(userId),
                        review.shop.id.gt(cursor == null ? 0 : cursor)
                )
                .orderBy(review.shop.id.asc())
                .limit(pageable.getPageSize())
                .fetch();
        List<String> shopPlaceIds = results.stream()
                .map(tuple -> tuple.get(review.shop.placeId))
                .collect(Collectors.toList());
        return shopPlaceIds;
    }

    private List<Long> findAllFollowerIds(Long userId) {
        List<FollowEntity> followers = queryFactory
                .selectFrom(follow)
                .innerJoin(follow.user, user).fetchJoin()
                .innerJoin(follow.follower, user).fetchJoin()
                .where(follow.user.id.eq(userId))
                .fetch();

        return followers.stream().map(o -> o.getFollower().getId()).collect(Collectors.toList());
    }

    private List<ReviewImageEntity> findAllReviewImages(Long reviewId) {
        return queryFactory
                .selectFrom(reviewImageEntity)
                .innerJoin(reviewImageEntity.image, image).fetchJoin()
                .where(reviewImageEntity.review.id.eq(reviewId))
                .fetch();
    }

    private BooleanExpression customCursor(Long idCursor, Integer rateCursor, String dateCursor, String sort, String direction) {
        if (idCursor == null) return null;
        StringExpression cursor = sort.equals("rate") ? review.rate.stringValue() :
                review.createdAt.stringValue().substring(2, 10);
        cursor = cursor.concat(StringExpressions.lpad(review.id.stringValue(), 5, '0'));
        String generateCursor = makeReviewCursor(idCursor, rateCursor, dateCursor, sort);
        if (direction.equals("desc")) {
            return cursor.lt(generateCursor);
        } else return cursor.gt(generateCursor);
    }

    private String makeReviewCursor(Long idCursor, Integer rateCursor, String dateCursor, String sort) {
        StringBuilder sb = new StringBuilder();
        if (sort.equals("rate")) sb.append(rateCursor == null ? "0" : rateCursor.toString());
        else sb.append(dateCursor == null ? "00-00-00" : dateCursor);
        sb.append(String.format("%1$" + 5 + "s", idCursor).replace(' ', '0'));
        return sb.toString();
    }

    private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier> orders = new ArrayList<>();

        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
            switch (order.getProperty()) {
                case "id":
                    OrderSpecifier<?> orderId = QueryDslUtil.getSortedColumn(direction, QReviewEntity.reviewEntity, "id");
                    orders.add(orderId);
                    break;
                case "createdAt":
                    OrderSpecifier<?> orderCreatedAt = QueryDslUtil.getSortedColumn(direction, QReviewEntity.reviewEntity, "createdAt");
                    orders.add(orderCreatedAt);
                    break;
                case "updatedAt":
                    OrderSpecifier<?> orderUpdatedAt = QueryDslUtil.getSortedColumn(direction, QReviewEntity.reviewEntity, "updatedAt");
                    orders.add(orderUpdatedAt);
                    break;
                case "rate":
                    OrderSpecifier<?> orderRate = QueryDslUtil.getSortedColumn(direction, QReviewEntity.reviewEntity, "rate");
                    orders.add(orderRate);
                    break;
                case "content":
                    OrderSpecifier<?> orderContent = QueryDslUtil.getSortedColumn(direction, QReviewEntity.reviewEntity, "content");
                    orders.add(orderContent);
                    break;
            }
        }
        return orders;
    }
}
