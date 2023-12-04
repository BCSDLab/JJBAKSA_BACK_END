package com.jjbacsa.jjbacsabackend.google.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShopScrapResponse {
    private String placeId;
    private String name;
    private String photo;
    private String category;
    private String address;
}
