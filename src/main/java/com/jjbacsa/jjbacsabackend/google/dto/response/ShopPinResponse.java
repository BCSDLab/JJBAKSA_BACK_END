package com.jjbacsa.jjbacsabackend.google.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ShopPinResponse {
    String placeId;
    String name;
    String category;
    List<String> photos;
}
