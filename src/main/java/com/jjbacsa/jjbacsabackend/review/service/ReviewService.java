package com.jjbacsa.jjbacsabackend.review.service;

import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewModifyRequest;
import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewModifyRequestDto;
import com.jjbacsa.jjbacsabackend.review.dto.request.ReviewRequest;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewDeleteResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface ReviewService {

    ReviewResponse createReview(ReviewRequest reviewRequest) throws Exception;
    ReviewResponse modifyReview(ReviewModifyRequest reviewModifyRequest) throws Exception;
    ReviewDeleteResponse deleteReview(Long reviewId) throws Exception;
    ReviewResponse getReview(Long reviewId);
    Page<ReviewResponse> searchShopReviews(Long shopId, Pageable pageable);
    Page<ReviewResponse> searchWriterReviews(Long writerId, Pageable pageable);
}
