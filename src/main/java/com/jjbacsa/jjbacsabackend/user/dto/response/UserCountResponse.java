package com.jjbacsa.jjbacsabackend.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCountResponse {
    @ApiModelProperty(notes = "유저 카운트 객체 고유 id", example = "1")
    private Long id;

    @ApiModelProperty(notes = "유저가 작성한 리뷰 수", example = "1")
    private Integer reviewCount = 0;

    @ApiModelProperty(notes = "유저의 Follow 수", example = "1")
    private Integer friendCount = 0;
}
