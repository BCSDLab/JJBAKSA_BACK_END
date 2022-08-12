package com.jjbacsa.jjbacsabackend.shop.dto;

import lombok.Data;

import java.util.List;

@Data
public class ShopResponse {

    private List<ShopDto> documents;
}
