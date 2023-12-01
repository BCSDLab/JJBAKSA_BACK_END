package com.jjbacsa.jjbacsabackend.google.dto.response;

import com.jjbacsa.jjbacsabackend.google.dto.api.inner.Geometry;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Coordinate {
    Double lng; //경도(x)
    Double lat; //위도(y)

    public static Coordinate from(Geometry geometry) {
        return new Coordinate(geometry.getLocation().getLng(), geometry.getLocation().getLat());
    }
}