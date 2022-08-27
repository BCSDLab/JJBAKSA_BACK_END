package com.jjbacsa.jjbacsabackend.review.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review.mapper.ReviewMapper;
import com.jjbacsa.jjbacsabackend.review_image.dto.response.ReviewImageResponse;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopReviewResponse;
import com.jjbacsa.jjbacsabackend.user.dto.response.UserReviewResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private String content;
    private int isTemp;
    private LocalDateTime createdAt;
    private List<ReviewImageResponse> reviewImages;
    private UserReviewResponse userReviewResponse;
    private ShopReviewResponse shopReviewResponse;


    // 생성 메서드 //
    public static ReviewResponse from(ReviewEntity reviewEntity){
        ReviewResponse response = ReviewMapper.INSTANCE.fromReviewEntity(reviewEntity);
        if(!reviewEntity.getReviewImages().isEmpty()) {
            response.reviewImages = new ArrayList<>();
            for (ReviewImageEntity image : reviewEntity.getReviewImages()) {
                ReviewImageResponse imageResponse = new ReviewImageResponse(image.getImage().getId(), image.getImage().getOriginalName(), image.getImage().getPath());
                response.getReviewImages().add(imageResponse);
            }
        }
        return response;
    }
}
