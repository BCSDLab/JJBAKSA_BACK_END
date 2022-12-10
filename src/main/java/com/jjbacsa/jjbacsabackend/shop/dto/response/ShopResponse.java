package com.jjbacsa.jjbacsabackend.shop.dto.response;

import com.jjbacsa.jjbacsabackend.shop.entity.ShopCount;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopResponse {
    private Long shopId;
    private String placeId;
    private String placeName;
    private String x;
    private String y;
    private String categoryName;
    private String phone;
    private String businessDay;

    private Integer totalRating;
    private Integer ratingCount;

    public boolean setShopCount(Integer totalRating, Integer ratingCount){
        try{
            this.totalRating=totalRating;
            this.ratingCount=ratingCount;

            return true;
        }catch (Exception e){
            return false;
        }
    }
    public static ShopResponse from(ShopEntity entity){
        return new ShopResponse(
                entity.getId(),
                entity.getPlaceId(),
                entity.getPlaceName(),
                entity.getX(),
                entity.getY(),
                entity.getCategoryName(),
                entity.getPhone(),
                entity.getBusinessDay(),
                entity.getShopCount().getTotalRating(),
                entity.getShopCount().getRatingCount()
        );
    }
}
