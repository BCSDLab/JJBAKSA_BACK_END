package com.jjbacsa.jjbacsabackend.shop.dto.shopDtoInner;

import lombok.Data;

@Data
public class Geometry {
    private Location location;

    @Data
    public static class Location{
        String lat;
        String lng;
    }
}
