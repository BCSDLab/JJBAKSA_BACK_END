package com.jjbacsa.jjbacsabackend.shop.dto.response;

import com.jjbacsa.jjbacsabackend.shop.entity.ShopCount;
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

    //ShopCount
    private Integer totalRating;
    private Integer ratingCount;

    public boolean setShopCount(Integer totalRating,Integer ratingCount){
        try{
            this.totalRating=totalRating;
            this.ratingCount=ratingCount;

            return true;
        }catch (Exception e){
            return false;
        }
    }
}
