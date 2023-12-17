package com.jjbacsa.jjbacsabackend.google.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jjbacsa.jjbacsabackend.google.dto.api.inner.Geometry;
import com.jjbacsa.jjbacsabackend.google.dto.api.inner.OpeningHours;
import com.jjbacsa.jjbacsabackend.google.dto.api.inner.Photo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * place Id로 얻어온 단일 상점 api
 * */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShopApiDto {

    @JsonProperty("place_id")
    private String placeId;
    private String name;

    @JsonProperty("formatted_address")
    private String formattedAddress;
    private Geometry geometry;

    @JsonProperty("formatted_phone_number")
    private String formattedPhoneNumber;

    @JsonProperty("opening_hours")
    private OpeningHours openingHours;
    private List<Photo> photos;
    private List<String> types;
}
