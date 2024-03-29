package com.jjbacsa.jjbacsabackend.shop.dto;


import com.jjbacsa.jjbacsabackend.shop.dto.shopInner.Geometry;
import com.jjbacsa.jjbacsabackend.shop.dto.shopInner.Opening_hours;
import com.jjbacsa.jjbacsabackend.shop.dto.shopInner.Photo;
import lombok.Data;

import java.util.List;

@Data
public class ShopApiDto {
    private String place_id;
    private String name;
    private String formatted_address;
    private List<String> types;
    private Geometry geometry;

    private String formatted_phone_number;
    private Opening_hours opening_hours;

    private List<Photo> photos;
}
