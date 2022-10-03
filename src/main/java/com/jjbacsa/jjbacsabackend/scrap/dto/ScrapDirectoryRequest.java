package com.jjbacsa.jjbacsabackend.scrap.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapDirectoryRequest {

    @ApiModelProperty(value = "스크랩 디렉토리 이름")
    private String name;
}
