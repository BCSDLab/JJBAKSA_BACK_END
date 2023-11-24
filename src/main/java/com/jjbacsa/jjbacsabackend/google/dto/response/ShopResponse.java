package com.jjbacsa.jjbacsabackend.google.dto.response;

import com.jjbacsa.jjbacsabackend.google.dto.inner.openingHours;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * shopDto(단일 상점 자세한 정보) 반환되는 클래스
 */
@Builder
@Getter
public class ShopResponse {
    private Long shopId;
    private String placeId;
    private String name;
    private String formattedAddress;
    private Double lat;
    private Double lng;
    private String formattedPhoneNumber;
    private Boolean openNow;
    private Integer totalRating;
    private Integer ratingCount;
    private String category;
    private Periods periods;
    private Long scrap;
    private List<String> photos;

    public void setShopCount(Integer totalRating, Integer ratingCount) {
        this.totalRating = totalRating;
        this.ratingCount = ratingCount;
    }

    public void setScrap(Long scrap) {
        this.scrap = scrap;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
}
