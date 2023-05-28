package com.jjbacsa.jjbacsabackend.google.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 상점 검색 결과(다중 상점) 반환
 * */
@Getter
@Builder
public class ShopQueryResponse {

    private String placeId;
    private String name;
    private String formattedAddress;
    private Double lat;
    private Double lng;
    private Boolean openNow;
    private Integer totalRating;
    private Integer ratingCount;
    private String photoToken;
    private Double dist;
    private String category;

    private List<String> photos;

    public void setShopCount(Integer totalRating, Integer ratingCount) {
        this.totalRating = totalRating;
        this.ratingCount = ratingCount;
    }
}
