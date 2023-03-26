package com.jjbacsa.jjbacsabackend.google.dto;

import com.jjbacsa.jjbacsabackend.google.dto.inner.Geometry;
import com.jjbacsa.jjbacsabackend.google.dto.inner.Opening_hours;
import com.jjbacsa.jjbacsabackend.google.dto.inner.Photos;
import lombok.Data;

import java.util.List;

/**
 * place Id로 얻어온 단일 상점 api
 * */

@Data
public class ShopApiDto {
    private String place_id;
    private String name;
    private String formatted_address;
    private Geometry geometry;
    private String formatted_phone_number;
    private Opening_hours opening_hours;
    private List<Photos> photos;
    private List<String> types;
}
