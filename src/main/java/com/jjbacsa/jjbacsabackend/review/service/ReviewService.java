package com.jjbacsa.jjbacsabackend.review.service;

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
    Page<ReviewResponse> searchShopReviews(Long shopId, Pageable pageable);
    Page<ReviewResponse> searchWriterReviews(Long writerId, Pageable pageable);
}
