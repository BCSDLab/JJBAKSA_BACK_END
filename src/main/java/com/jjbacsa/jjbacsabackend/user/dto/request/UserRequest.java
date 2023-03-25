package com.jjbacsa.jjbacsabackend.user.dto.request;

import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @ApiModelProperty(example="test123")
    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Login.class}, message = "아이디를 비워둘 순 없습니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,20}$", message = "올바른 형식의 아이디가 아닙니다.",
            groups = ValidationGroups.Create.class)
    private String account;

    @ApiModelProperty(example="test1234!")
    @Pattern(regexp = "(?=[0-9a-zA-z~!@#$%^&*()\\-_=+]*[0-9])(?=[0-9a-zA-z~!@#$%^&*()\\-_=+]*[a-zA-z])(?=[0-9a-zA-z~!@#$%^&*()\\-_=+]*[~!@#$%^&*()\\-_=+]).{8,16}",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}, message = "올바른 형식의 비밀번호가 아닙니다.")
    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Login.class}, message = "비밀번호를 비워둘 순 없습니다.")
    private String password;

    @ApiModelProperty(example="test@naver.com")
    @Email(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}, message = "올바르지 않은 이메일입니다.")
    private String email;

    //TODO : 닉네임 관련 추가 정보 필요
    @ApiModelProperty(example="테스트")
    @Pattern(regexp = "^[a-zA-z가-힣0-9]{1,20}$",
            groups = {ValidationGroups.Update.class}, message = "닉네임에 특수문자와 초성은 불가능합니다.")
    private String nickname;
}
