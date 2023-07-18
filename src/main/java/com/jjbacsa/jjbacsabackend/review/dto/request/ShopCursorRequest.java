package com.jjbacsa.jjbacsabackend.review.dto.request;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Range;

import javax.annotation.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Getter
public class ShopCursorRequest {

    @Nullable
    private Long cursor;

    @Builder.Default
    @ApiParam("조회할 개수")
    @Range(min = 1, max = 10, message = "size의 범위는 1~10 사이 입니다.")
    private int size = 3;

}
