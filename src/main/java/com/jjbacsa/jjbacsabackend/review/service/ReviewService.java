package com.jjbacsa.jjbacsabackend.review.service;

<<<<<<< HEAD
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
=======
import com.jjbacsa.jjbacsabackend.review.dto.ReviewDto;
import com.jjbacsa.jjbacsabackend.review.dto.ReviewWithImageDto;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewResponse;
import com.jjbacsa.jjbacsabackend.review.dto.response.ReviewWithImageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ReviewWithImageResponse createReview(ReviewWithImageDto reviewWithImageDto);
    ReviewWithImageResponse modifyReview(ReviewWithImageDto reviewWithImageDto);
    void deleteReview(Long reviewId);
    ReviewWithImageResponse getReview(Long reviewId);
>>>>>>> review
    Page<ReviewResponse> searchShopReviews(Long shopId, Pageable pageable);
    Page<ReviewResponse> searchWriterReviews(Long writerId, Pageable pageable);
}
