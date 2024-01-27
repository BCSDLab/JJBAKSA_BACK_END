package com.jjbacsa.jjbacsabackend.google.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ShopSimpleResponse {
    private String placeId;
    private String name;
    private Coordinate coordinate;

    private String category;
    private List<String> photos;
    private ShopRateResponse rate;
    private Boolean openNow;
    private String formattedAddress;
    private String simpleFormattedAddress;
    private Double dist;

    public void setDist(Double dist){
        this.dist = dist;
    }
}
