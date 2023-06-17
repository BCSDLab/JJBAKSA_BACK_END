package com.jjbacsa.jjbacsabackend.post.dto.request;

import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    @ApiModelProperty(example = "title")
    @NotNull(groups = {ValidationGroups.AdminCreate.class})
    private String title;

    @ApiModelProperty(example = "content")
    @NotNull(groups = {ValidationGroups.AdminCreate.class})
    private String content;

    @ApiModelProperty(example = "NOTICE")
    @Pattern(regexp = "^(NOTICE|POWER_NOTICE)$", message = "올바른 게시글 타입이 아닙니다.",
            groups = {ValidationGroups.AdminCreate.class})
    @NotNull(groups = {ValidationGroups.AdminCreate.class}, message = "게시글 타입을 설정해야합니다.")
    private String boardType;
}
