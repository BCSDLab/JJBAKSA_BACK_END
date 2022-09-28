package com.jjbacsa.jjbacsabackend.review.service;

import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewModifyRequest;
import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewRequest;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewDeleteResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ReviewResponse createReview(ReviewRequest reviewRequest) throws Exception;
    ReviewResponse modifyReview(ReviewModifyRequest reviewModifyRequest) throws Exception;
    ReviewDeleteResponse deleteReview(Long reviewId) throws Exception;
    ReviewResponse getReview(Long reviewId);
    Page<ReviewResponse> searchShopReviews(Long shopId, Pageable pageable);
    Page<ReviewResponse> searchWriterReviews(Long writerId, Pageable pageable);

    Page<ReviewResponse> getMyReviews(Pageable pageable) throws Exception;
    Page<ReviewResponse> getFollowersReviews(Pageable pageable) throws Exception;
    Page<ReviewResponse> searchFollowerReviews(String followerAccount, Pageable pageable) throws Exception;
    Page<ReviewResponse> searchFollowersShopReviews(Long shopId, Pageable pageable) throws Exception;
}
