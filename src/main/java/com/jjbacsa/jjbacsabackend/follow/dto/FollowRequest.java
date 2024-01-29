package com.jjbacsa.jjbacsabackend.follow.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowRequest {

    @ApiModelProperty(
            value = "요청을 받을 사용자의 계정\n\n" +
                    "또는 제거할 팔로워의 계정")
    private String userAccount;
}
