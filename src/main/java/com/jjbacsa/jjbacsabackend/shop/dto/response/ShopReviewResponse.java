package com.jjbacsa.jjbacsabackend.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopReviewResponse {
    private Long id;
    private String placeId;
    private String placeName;
    private String categoryName;
}
