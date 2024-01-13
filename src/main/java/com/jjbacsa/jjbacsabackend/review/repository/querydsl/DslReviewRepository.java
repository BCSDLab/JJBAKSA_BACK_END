package com.jjbacsa.jjbacsabackend.review.repository.querydsl;


import com.jjbacsa.jjbacsabackend.google.dto.response.ShopIdPair;
import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewCursorRequest;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface DslReviewRepository {
    ReviewEntity findByReviewId(Long reviewId);

    Page<ReviewEntity> findAllByShopPlaceId(Long userId, String placeId, ReviewCursorRequest request);

    Page<ReviewEntity> findAllFollowerReviewsByShopPlaceId(Long followerId, String placeId, ReviewCursorRequest request);

    Page<ReviewEntity> findAllFollowersReviewsByShopPlaceId(Long userId, String placeId, ReviewCursorRequest request);

    List<ShopIdPair> findShopPlaceIdsByMyReviews(Long userId, Long cursor, Pageable pageable) throws Exception;

    Long getReviewCount(Long userId);

    Long getFollowersReviewCountByShop(Long userId, String placeId);

    Date getFollowersReviewLastDateByShop(Long userId, String placeId);

    Date getReviewLastDateByShop(Long userId, String placeId);
}
