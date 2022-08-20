package com.jjbacsa.jjbacsabackend.review.dto.request;

import com.jjbacsa.jjbacsabackend.review_image.dto.ReviewImageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {

    private String content;
    private int isTemp;
    private List<ReviewImageDto> reviewImageDtos;
}
