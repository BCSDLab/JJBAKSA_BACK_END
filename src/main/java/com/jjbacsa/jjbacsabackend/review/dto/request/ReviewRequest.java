package com.jjbacsa.jjbacsabackend.review.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

    private Long shopId;
    private String content;
    private int isTemp;
    private List<MultipartFile> reviewImages;

    public static ReviewRequest createReviewRequest(List<MultipartFile> reviewImages, ReviewRequestDto reviewRequestDto){
        ReviewRequest reviewRequest = new ReviewRequest();
        if(reviewImages != null) reviewRequest.setReviewImages(reviewImages);
        reviewRequest.setShopId(reviewRequestDto.getShopId());
        reviewRequest.setContent(reviewRequestDto.getContent());
        reviewRequest.setIsTemp(reviewRequestDto.getIsTemp());
        return reviewRequest;
    }
}
