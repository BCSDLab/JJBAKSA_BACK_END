package com.jjbacsa.jjbacsabackend.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopDto {
    private Long id;
    private String placeId;
    private String placeName;
    private String categoryName;
}
