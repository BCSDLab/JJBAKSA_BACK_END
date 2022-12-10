package com.jjbacsa.jjbacsabackend.review.service;

import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewRequest;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewDeleteResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ReviewResponse createReview(ReviewRequest reviewRequest) throws Exception;
    ReviewResponse modifyReview(ReviewRequest reviewRequest, Long reviewId) throws Exception;
    ReviewDeleteResponse deleteReview(Long reviewId) throws Exception;
    ReviewResponse getReview(Long reviewId);
    Page<ReviewResponse> searchShopReviews(Long shopId, Integer page, Integer size);
    Page<ReviewResponse> searchWriterReviews(Long writerId, Integer page, Integer size);

    Page<ReviewResponse> getMyReviews(Integer page, Integer size) throws Exception;
    Page<ReviewResponse> getFollowersReviews(Integer page, Integer size) throws Exception;
    Page<ReviewResponse> searchFollowerReviews(String followerAccount, Integer page, Integer size) throws Exception;
    Page<ReviewResponse> searchFollowersShopReviews(Long shopId, Integer page, Integer size) throws Exception;

    Page<ShopResponse> searchShopByReviewContentsAndFollowers (String cursor, String searchWord, Integer size) throws Exception;
}
