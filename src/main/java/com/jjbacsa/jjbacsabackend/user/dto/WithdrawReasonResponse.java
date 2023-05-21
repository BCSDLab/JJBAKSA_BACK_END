package com.jjbacsa.jjbacsabackend.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawReasonResponse {

    @ApiModelProperty(notes = "탈퇴 대상 id", example = "1")
    private Long id;

    @ApiModelProperty(notes = "저장된 탈퇴 사유", example = "string")
    private String reason;

    @ApiModelProperty(notes = "저장된 개선 바라는 점", example = "string")
    private String discomfort;

    public WithdrawReasonResponse(Long id, WithdrawRequest request) {
        this.id = id;
        this.reason = request.getReason();
        this.discomfort = request.getDiscomfort();
    }
}
