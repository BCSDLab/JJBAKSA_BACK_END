package com.jjbacsa.jjbacsabackend.google.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * shopDto(단일 상점 자세한 정보) 반환되는 클래스
 */

@Builder
@Getter
public class ShopResponse {
    private String placeId;
    private String name;
    private String formattedAddress;
    private Double lat;
    private Double lng;
    private String formattedPhoneNumber;
    private Boolean openNow;
    private String businessDay;
    private Integer totalRating;
    private Integer ratingCount;
    private String photoToken;
    private String category;
    private String todayBusinessHour; //오늘 영업시간
    private boolean isScrap;

    public void setShopCount(Integer totalRating, Integer ratingCount) {
        this.totalRating = totalRating;
        this.ratingCount = ratingCount;
    }

    public void setIsScrap(boolean isScrap) {
        this.isScrap = isScrap;
    }
}
