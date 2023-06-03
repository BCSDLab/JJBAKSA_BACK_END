package com.jjbacsa.jjbacsabackend.google.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

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
    private String category;
    private String todayBusinessHour; //오늘 영업시간
    private boolean isScrap;
    private List<String> photos;
    private LocalDateTime lastReviewDate;

    public void setShopCount(Integer totalRating, Integer ratingCount) {
        this.totalRating = totalRating;
        this.ratingCount = ratingCount;
    }

    public void setIsScrap(boolean isScrap) {
        this.isScrap = isScrap;
    }
}
