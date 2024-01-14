package com.jjbacsa.jjbacsabackend.google.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ShopIdPair {
    private Long id;
    private String placeId;
}
