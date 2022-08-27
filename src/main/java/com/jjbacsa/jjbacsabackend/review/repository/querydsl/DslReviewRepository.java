package com.jjbacsa.jjbacsabackend.review.repository.querydsl;


import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;

public interface DslReviewRepository {
    ReviewEntity findByReviewId(Long reviewId);
}
