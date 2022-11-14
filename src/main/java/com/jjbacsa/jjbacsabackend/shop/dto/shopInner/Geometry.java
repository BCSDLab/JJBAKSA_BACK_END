package com.jjbacsa.jjbacsabackend.shop.dto.shopInner;

import lombok.Data;

@Data
public class Geometry {
    private Location location;

    @Data
    public static class Location{
        String lng; //경도(x)
        String lat; //위도(y)
    }
}
