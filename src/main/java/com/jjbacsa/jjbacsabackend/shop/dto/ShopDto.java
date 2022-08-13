package com.jjbacsa.jjbacsabackend.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ShopDto {
    private Long id;
    private String placeId;
    private String placeName;
    private String categoryName;
}
