package com.jjbacsa.jjbacsabackend.google.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ShopScrapResponse {
    private String placeId;
    private String name;
    private String photo;
    private String category;
    private Integer totalRating;
    private Integer ratingCount;
    private Long scrapId;
    private String address;

    public void setShopCount(Integer totalRating, Integer ratingCount) {
        this.totalRating = totalRating;
        this.ratingCount = ratingCount;
    }
}
