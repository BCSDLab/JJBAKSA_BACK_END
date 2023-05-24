package com.jjbacsa.jjbacsabackend.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Query 로 얻어온 다중 상점 파싱
 * */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopQueryDto {

    @JsonProperty("next_page_token")
    String nextPageToken;

    List<ShopQueryApiDto> results;
}
