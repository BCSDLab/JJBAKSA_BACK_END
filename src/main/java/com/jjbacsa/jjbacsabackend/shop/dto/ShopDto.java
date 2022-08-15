package com.jjbacsa.jjbacsabackend.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ShopDto {
    private String placeId;
    private String placeName;
    private String x;
    private String y;
    private String categoryName;
    private String address;
    private String phone;
    private String businessDay;
}