package com.jjbacsa.jjbacsabackend.google.dto.response;

import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopCount;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShopRateResponse {
    private Integer totalRating;
    private Integer ratingCount;

    public static ShopRateResponse from(GoogleShopCount countEntity) {
        return new ShopRateResponse(countEntity.getTotalRating(), countEntity.getRatingCount());
    }

    public static ShopRateResponse createDefaultRateResponse() {
        return new ShopRateResponse(0, 0);
    }
}