package com.jjbacsa.jjbacsabackend.google.dto.api.inner;

import lombok.Data;

@Data
public class Geometry {
    private Location location;
    @Data
    public static class Location{
        Double lng; //경도(x)
        Double lat; //위도(y)
    }
}
