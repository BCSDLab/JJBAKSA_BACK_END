package com.jjbacsa.jjbacsabackend.review.service;

import com.jjbacsa.jjbacsabackend.google.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.review.dto.request.*;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewCountResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewDateResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewDeleteResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;

public interface ReviewService {

    ReviewResponse create(ReviewRequest reviewRequest) throws Exception;

    ReviewResponse modify(ReviewRequest reviewRequest, Long reviewId) throws Exception;

    ReviewDeleteResponse delete(Long reviewId) throws Exception;

    ReviewResponse get(Long reviewId) throws Exception;

    Page<ReviewResponse> getMyReviewsByShop(ReviewCursorRequest reviewCursorRequest, String placeId) throws Exception;

    Page<ReviewResponse> getFollowersReviewsByShop(ReviewCursorRequest reviewCursorRequest, String placeId) throws Exception;

    Page<ReviewResponse> getFollowerReviewsByShop(ReviewCursorRequest reviewCursorRequest, Long followerId, String placeId) throws Exception;

    Page<ShopResponse> getShopsByMyReviews(ShopCursorRequest shopCursorRequest) throws Exception;

    Page<ShopResponse> getShopsByFollowerReviews(ShopCursorRequest shopCursorRequest, Long followerId) throws Exception;

    ReviewCountResponse getMyReviewCount() throws Exception;

    ReviewCountResponse getFollowerReviewCount(Long followerId) throws Exception;

    ReviewCountResponse getFollowersReviewCount(String placeId) throws Exception;

    ReviewDateResponse getFollowerReviewLastDateByShop(String placeId) throws Exception;

    ReviewDateResponse getReviewLastDateByShop(String placeId) throws Exception;
}
