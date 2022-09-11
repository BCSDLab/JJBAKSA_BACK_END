package com.jjbacsa.jjbacsabackend.review.dto.response;

import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review.mapper.ReviewMapper;
import com.jjbacsa.jjbacsabackend.review_image.dto.response.ReviewImageResponse;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopReviewResponse;
import com.jjbacsa.jjbacsabackend.user.dto.response.UserReviewResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDeleteResponse {
    private Long id;
    private String content;
    private int isTemp;
    private int isDeleted;
    private UserReviewResponse userReviewResponse;
    private ShopReviewResponse shopReviewResponse;

    // 생성 메서드
    public static ReviewDeleteResponse from(ReviewEntity reviewEntity){
        ReviewDeleteResponse response = ReviewMapper.INSTANCE.fromReviewEntityToDelete(reviewEntity);
        return response;
    }
}
