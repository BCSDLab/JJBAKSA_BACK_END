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
    @NotNull(groups = {ValidationGroups.AdminCreate.class, ValidationGroups.NormalCreate.class})
    private String title;

    @ApiModelProperty(example = "content")
    @NotNull(groups = {ValidationGroups.AdminCreate.class, ValidationGroups.NormalCreate.class})
    private String content;

    @ApiModelProperty(example = "NOTICE")
    @Pattern(regexp = "^(FAQ|NOTICE|INQUIRY|POWER_NOTICE)$", message = "올바른 게시글 타입이 아닙니다.",
            groups = {ValidationGroups.AdminCreate.class})
    @Pattern(regexp = "^(INQUIRY)$", message = "올바른 게시글 타입이 아닙니다.",
            groups = {ValidationGroups.NormalCreate.class})
    @NotNull(groups = {ValidationGroups.Create.class})
    private String boardType;
}
