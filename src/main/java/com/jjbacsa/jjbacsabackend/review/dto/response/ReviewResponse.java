package com.jjbacsa.jjbacsabackend.review.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jjbacsa.jjbacsabackend.review_image.dto.response.ReviewImageResponse;
import com.jjbacsa.jjbacsabackend.user.dto.response.UserReviewResponse;
import lombok.*;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Builder
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private String content;
    private Integer rate;
    @JsonFormat(pattern = "yy-MM-dd", timezone = "Asia/Seoul")
    private Date createdAt;
    private List<ReviewImageResponse> reviewImages;
    private UserReviewResponse userReviewResponse;
    private String shopPlaceId;

}
