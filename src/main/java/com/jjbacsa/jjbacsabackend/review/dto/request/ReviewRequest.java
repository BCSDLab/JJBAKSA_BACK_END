package com.jjbacsa.jjbacsabackend.review.dto.request;

import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
    @NotNull(groups = {ValidationGroups.Create.class}, message = "상점 id가 필요합니다.")
    private Long shopId;
    @NotNull(groups = {ValidationGroups.Create.class}, message = "리뷰 내용을 입력해주세요.")
    private String content;
    @Range(min = 0, max = 5, message = "별점의 범위는 0 ~ 5점 까지 입니다.")
    @NotNull(groups = {ValidationGroups.Create.class}, message = "별점을 입력해주세요.")
    private Integer rate;
    private List<MultipartFile> reviewImages;
}
