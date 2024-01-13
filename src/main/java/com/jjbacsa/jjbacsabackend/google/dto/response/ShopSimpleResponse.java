package com.jjbacsa.jjbacsabackend.google.dto.response;

import com.jjbacsa.jjbacsabackend.google.dto.api.inner.Geometry;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ShopSimpleResponse {
    private String placeId;
    private String name;
    private Coordinate coordinate;
    private String photo;
}
