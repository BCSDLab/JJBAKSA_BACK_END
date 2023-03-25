package com.jjbacsa.jjbacsabackend.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.image.dto.response.ImageResponse;
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
public class UserResponse {
    @ApiModelProperty(notes = "유저 고유 id", example = "1")
    private Long id;

    @ApiModelProperty(notes = "유저 계정(영문/숫자 1~20글자)", example = "jjbcsa123")
    private String account;

    @ApiModelProperty(notes = "유저 닉네임(영문/한글/숫자 1~20글자)", example = "쩝쩝bacsa123")
    private String nickname;

    @ApiModelProperty(notes = "유저 계정", example = "jjbcsa@naver.com")
    private String email;

    private ImageResponse profileImage;

    @ApiModelProperty(notes = "OAuth 타입", example = "NAVER")
    private OAuthType oAuthType;

    @ApiModelProperty(notes = "유저 타입", example = "NORMAL")
    private UserType userType;

    private UserCountResponse userCountResponse;
}
