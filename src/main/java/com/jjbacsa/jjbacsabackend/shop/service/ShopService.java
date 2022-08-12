package com.jjbacsa.jjbacsabackend.shop.service;

import com.jjbacsa.jjbacsabackend.shop.dto.ShopDto;

import java.util.List;

public interface ShopService {
    ShopDto getShop(String place_id);
    ShopDto.Shop searchShop(String place_id);
}
