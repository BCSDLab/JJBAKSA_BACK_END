package com.jjbacsa.jjbacsabackend.shop.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopRequest {

    private String placeId;
    private String placeName;
    private String x;
    private String y;
    private String categoryName;
}
