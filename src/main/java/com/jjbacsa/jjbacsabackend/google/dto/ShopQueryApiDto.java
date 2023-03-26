package com.jjbacsa.jjbacsabackend.google.dto;

import com.jjbacsa.jjbacsabackend.google.dto.inner.Geometry;
import com.jjbacsa.jjbacsabackend.google.dto.inner.Opening_hours;
import com.jjbacsa.jjbacsabackend.google.dto.inner.Photos;
import lombok.Data;

import java.util.List;


/**
 * Query 로 얻어온 다중 상점 중 각 객체
 * */

@Data
public class ShopQueryApiDto {
    private String place_id;
    private String name;
    private String formatted_address;
    private Geometry geometry;
    private Opening_hours opening_hours;
    private List<Photos> photos;
    private List<String> types;
}
