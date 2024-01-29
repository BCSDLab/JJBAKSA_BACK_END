package com.jjbacsa.jjbacsabackend.etc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Token {
    @ApiModelProperty(notes = "Access Token", example = "Access Token")
    private String accessToken;

    @ApiModelProperty(notes = "Refresh Token", example = "Refresh Token")
    private String refreshToken;
}
