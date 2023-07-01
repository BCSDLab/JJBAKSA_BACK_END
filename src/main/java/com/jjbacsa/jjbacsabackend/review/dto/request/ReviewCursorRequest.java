package com.jjbacsa.jjbacsabackend.review.dto.request;

import io.swagger.annotations.ApiParam;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Nullable;
import javax.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCursorRequest {

    @Nullable
    @ApiParam("조회한 마지막 리뷰 id, 첫 조회는 null")
    private Long idCursor;

    @Nullable
    @ApiParam("조회한 마지막 리뷰 createdAt, 정렬 기준이 createdAt일 때 입력")
    private String dateCursor;

    @Nullable
    @ApiParam("조회한 마지막 리뷰 rate, 정렬 기준이 rate일 때 입력")
    private Integer rateCursor;

    @Builder.Default
    @ApiParam("페이지 크기")
    @Range(min = 1, max = 10, message = "size의 범위는 1~10 사이 입니다.")
    private int size = 3;

    @Builder.Default
    @ApiParam("정렬방식 - 내림차순, 오름차순")
    @Pattern(regexp = "^(asc|desc)$", message = "올바른 정렬 방식이 아닙니다.")
    private String direction = "desc";

    @Builder.Default
    @ApiParam("정렬 기준 지정")
    @Pattern(regexp = "^(createdAt|rate)$", message = "올바른 정렬 형식이 아닙니다.")
    private String sort = "createdAt";

    public PageRequest of() {
        List<String> sorts = null;
        if (this.sort.equals("rate")) {
            sorts = Arrays.asList("rate", "id");
        } else {
            sorts = Arrays.asList("createdAt", "id");
        }
        return PageRequest.of(0,
                size,
                direction == "desc" ? Sort.Direction.DESC : Sort.Direction.ASC,
                sorts.toArray(String[]::new));
    }
}
