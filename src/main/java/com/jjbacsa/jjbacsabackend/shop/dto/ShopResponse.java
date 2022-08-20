package com.jjbacsa.jjbacsabackend.shop.dto;

import com.jjbacsa.jjbacsabackend.shop.entity.ShopCount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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
    private ShopCount shopCount;
}
