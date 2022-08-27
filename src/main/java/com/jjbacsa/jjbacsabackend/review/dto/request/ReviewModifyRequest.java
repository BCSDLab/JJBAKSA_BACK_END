package com.jjbacsa.jjbacsabackend.review.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewModifyRequest {
    private Long id;
    private Long shopId;
    private String content;
    private int isTemp;
    private List<MultipartFile> reviewImages;

    public static ReviewModifyRequest createReviewModifyRequest(List<MultipartFile> reviewImages, ReviewModifyRequestDto reviewModifyRequestDto){
        ReviewModifyRequest reviewModifyRequest = new ReviewModifyRequest();
        if(!reviewImages.isEmpty()) reviewModifyRequest.setReviewImages(reviewImages);
        reviewModifyRequest.setId(reviewModifyRequestDto.getId());
        reviewModifyRequest.setShopId(reviewModifyRequestDto.getShopId());
        reviewModifyRequest.setContent(reviewModifyRequestDto.getContent());
        reviewModifyRequest.setIsTemp(reviewModifyRequestDto.getIsTemp());
        return reviewModifyRequest;
    }
}
