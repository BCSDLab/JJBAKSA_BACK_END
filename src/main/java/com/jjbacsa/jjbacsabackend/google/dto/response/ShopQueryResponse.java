package com.jjbacsa.jjbacsabackend.google.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 상점 검색 결과(다중 상점) 반환
 */
@Getter
@Builder
public class ShopQueryResponse {
    private String placeId;
    private String name;
    private String formattedAddress;
    private String simpleFormattedAddress;
    private Coordinate coordinate;
    private Boolean openNow;
    private String photoToken;
    private Double dist;
    private String category;
}
