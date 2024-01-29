package com.jjbacsa.jjbacsabackend.user.dto;

import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserModifyRequest {

    // TODO : 2차 배포 아이디 변경 추가
    @ApiModelProperty(example="test1234!")
    @Pattern(regexp = "(?=[0-9a-zA-z~!@#$%^&*()\\-_=+]*[0-9])(?=[0-9a-zA-z~!@#$%^&*()\\-_=+]*[a-zA-z])(?=[0-9a-zA-z~!@#$%^&*()\\-_=+]*[~!@#$%^&*()\\-_=+]).{8,16}",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}, message = "올바른 형식의 비밀번호가 아닙니다.")
    private String password;

    @ApiModelProperty(example="닉네임")
    @Pattern(regexp = "^[a-zA-z가-힣0-9]{1,20}$",
            groups = {ValidationGroups.Update.class}, message = "닉네임에 특수문자와 초성은 불가능합니다.")
    private String nickname;

}
