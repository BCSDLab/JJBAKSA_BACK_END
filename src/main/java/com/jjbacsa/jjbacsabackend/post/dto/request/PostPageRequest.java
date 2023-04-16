package com.jjbacsa.jjbacsabackend.post.dto.request;

import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import io.swagger.annotations.ApiParam;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostPageRequest {

    @Nullable
    @ApiParam("조회한 Post 마지막 생성일")
    String cursor;

    @Builder.Default
    @ApiParam("조회할 개수")
    @Range(min = 1, max = 100, message = "size의 범위는 1 ~ 100 입니다.")
    Integer size = 3;

    @Pattern(regexp = "^(NOTICE|POWER_NOTICE)$", message = "올바른 게시글 타입이 아닙니다.")
    @NotNull(message = "게시글 타입을 설정해야합니다.")
    String boardType;
}
