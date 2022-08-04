package com.jjbacsa.jjbacsabackend.shop.dto;

import lombok.Data;

@Data
public class ShopDto{
    private String id;
    private String place_name;
    private String category_group_code;
    private String phone;
    private String address_name;
    private String road_address_name;
    private String x;
    private String y;
    private String place_url;
    private String distance;
}
