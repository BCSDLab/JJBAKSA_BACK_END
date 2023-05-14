package com.jjbacsa.jjbacsabackend.google.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 상점 검색 결과(다중 상점) 반환
 * */
@Getter
@Builder
public class ShopQueryResponse {

    private String place_id;
    private String name;
    private String formatted_address;
    private Double x;
    private Double y;
    private Boolean open_now;
    private Integer totalRating;
    private Integer ratingCount;
    private String photoToken;
    private Double dist;
    private String category;

    public void setShopCount(Integer totalRating, Integer ratingCount) {
        this.totalRating = totalRating;
        this.ratingCount = ratingCount;
    }
}
