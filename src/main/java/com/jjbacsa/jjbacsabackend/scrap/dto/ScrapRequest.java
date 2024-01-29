package com.jjbacsa.jjbacsabackend.scrap.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class ScrapRequest {

    @ApiModelProperty(
            value = "스크랩을 저장할 폴더 ID\n\n" +
                    "0이면 root")
    private Long directoryId;

    @ApiModelProperty(value = "스크랩할 place ID\n\n")
    private String placeId;
}
