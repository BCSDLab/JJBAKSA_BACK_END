package com.jjbacsa.jjbacsabackend.shop.dto.shopInner;

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