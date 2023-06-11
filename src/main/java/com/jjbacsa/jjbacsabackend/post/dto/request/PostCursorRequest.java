package com.jjbacsa.jjbacsabackend.post.dto.request;

import io.swagger.annotations.ApiParam;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.annotation.Nullable;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCursorRequest {

    @Nullable
    @ApiParam("조회한 마지막 Post의 Id")
    private Long idCursor;

    @Nullable
    @ApiParam("조회한 마지막 Post의 생성일")
    private String dateCursor;


    @Builder.Default
    @ApiParam("조회할 개수")
    @Range(min = 1, max = 100, message = "size의 범위는 1 ~ 100 입니다.")
    private int size = 3;

}
