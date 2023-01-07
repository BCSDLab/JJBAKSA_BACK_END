package com.jjbacsa.jjbacsabackend.review.repository.querydsl;


import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DslReviewRepository {
    ReviewEntity findByReviewId(Long reviewId);
    Page<ReviewEntity> findAllByFollowerId(Long followerId, Pageable pageable);
    Page<ReviewEntity> findAllFriendsReview(Long userId, Pageable pageable);
    Page<ReviewEntity> findAllFollowersReviewsByShopId(Long userId, Long shopId, Pageable pageable);
    Page<ShopEntity> findAllShopBySearchWordInReviewWithCursor(String cursor, Long userId, String searchWord, Pageable pageable);
}
