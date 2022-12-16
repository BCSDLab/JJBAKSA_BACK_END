package com.jjbacsa.jjbacsabackend.user.dto;

import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {

    @ApiModelProperty(example="test123")
    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Login.class}, message = "아이디를 비워둘 순 없습니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,20}$", message = "올바른 형식의 아이디가 아닙니다.",
            groups = ValidationGroups.Create.class)
    private String account;

    @ApiModelProperty(example="test@naver.com")
    @Email(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}, message = "올바르지 않은 이메일입니다.")
    private String email;

    @ApiModelProperty(example="1234")
    @NotNull
    private String code;

    @ApiModelProperty(example="test1234!")
    @Pattern(regexp = "(?=[0-9a-zA-z~!@#$%^&*()\\-_=+]*[0-9])(?=[0-9a-zA-z~!@#$%^&*()\\-_=+]*[a-zA-z])(?=[0-9a-zA-z~!@#$%^&*()\\-_=+]*[~!@#$%^&*()\\-_=+]).{8,16}",
            groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}, message = "올바른 형식의 비밀번호가 아닙니다.")
    @NotNull(groups = {ValidationGroups.Create.class, ValidationGroups.Login.class}, message = "비밀번호를 비워둘 순 없습니다.")
    private String password;

}
