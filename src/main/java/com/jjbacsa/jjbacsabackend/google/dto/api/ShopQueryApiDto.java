package com.jjbacsa.jjbacsabackend.google.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jjbacsa.jjbacsabackend.google.dto.api.inner.Geometry;
import com.jjbacsa.jjbacsabackend.google.dto.api.inner.OpeningHours;
import com.jjbacsa.jjbacsabackend.google.dto.api.inner.Photo;
import lombok.Data;

import java.util.List;


/**
 * Query 로 얻어온 다중 상점 중 각 객체
 * */

@Data
public class ShopQueryApiDto {

    @JsonProperty("place_id")
    private String placeId;
    private String name;

    @JsonProperty("formatted_address")
    private String formattedAddress;
    private Geometry geometry;

    @JsonProperty("opening_hours")
    private OpeningHours openingHours;
    private List<Photo> photos;
    private List<String> types;
}
