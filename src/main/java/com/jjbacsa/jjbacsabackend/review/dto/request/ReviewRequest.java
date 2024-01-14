package com.jjbacsa.jjbacsabackend.review.dto.request;

import com.jjbacsa.jjbacsabackend.etc.annotations.IsValidListSize;
import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class ReviewRequest {
    private String content;
    @NotNull(message = "상점 id가 필요합니다.")
    private String placeId;
    @Range(min = 0, max = 5, message = "별점의 범위는 0 ~ 5점 까지 입니다.")
    @NotNull(message = "별점을 입력해주세요.")
    private Integer rate;
    @IsValidListSize(max = 10, message = "리뷰 이미지는 최대 10개 입니다.")
    private List<MultipartFile> reviewImages;
}
