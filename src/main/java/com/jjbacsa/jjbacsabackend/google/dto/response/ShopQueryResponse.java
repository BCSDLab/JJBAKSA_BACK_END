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

    //todo: 법정동 주소 반환?
    private String formattedAddress;

    //todo: 원자성
    private Double lat;
    private Double lng;

    private Boolean openNow;

    private String photoToken;

    private Double dist;

    private String category;
}
