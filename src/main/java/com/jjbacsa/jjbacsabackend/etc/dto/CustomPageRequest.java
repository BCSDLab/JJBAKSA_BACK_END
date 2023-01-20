package com.jjbacsa.jjbacsabackend.etc.dto;

import io.swagger.annotations.ApiParam;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.Arrays;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomPageRequest {
    @Builder.Default
    @ApiParam("조회할 페이지")
    @Min(value = 0, message = "page는 0이상 입니다.")
    private int page = 0;
    @Builder.Default
    @ApiParam("페이지 크기")
    @Range(min = 1, max = 100, message = "size의 범위는 1~100 사이 입니다.")
    private int size = 10;
    @Builder.Default
    @ApiParam("정렬방식 - 내림차순, 오름차순")
    private Sort.Direction direction = Sort.Direction.DESC;
    @Builder.Default
    @ApiParam("정렬 기준 지정")
    @Pattern(regexp= "^[a-zA-Z,\\s]*$", message = "올바른 정렬 형식이 아닙니다.")
    private String sort = "createdAt,id";

    public PageRequest of(){
        return PageRequest.of(page, size, direction,
                Arrays.stream(sort.split(","))
                        .map(s-> s.trim())
                        .toArray(String[]::new));
    }
}
