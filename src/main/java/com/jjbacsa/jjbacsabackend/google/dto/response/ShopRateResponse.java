package com.jjbacsa.jjbacsabackend.google.dto.response;

import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopCount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShopRateResponse {
    @Schema(description = "전체 평점 합산", defaultValue = "0")
    private Integer totalRating;

    @Schema(description = "평가 횟수 합산", defaultValue = "0")
    private Integer ratingCount;

    public static ShopRateResponse from(GoogleShopCount countEntity) {
        return new ShopRateResponse(countEntity.getTotalRating(), countEntity.getRatingCount());
    }

    public static ShopRateResponse createDefaultRateResponse() {
        return new ShopRateResponse(0, 0);
    }
}