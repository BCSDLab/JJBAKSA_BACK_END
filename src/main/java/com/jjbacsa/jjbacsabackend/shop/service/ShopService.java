package com.jjbacsa.jjbacsabackend.shop.service;

import com.jjbacsa.jjbacsabackend.shop.dto.ShopDto;
import com.jjbacsa.jjbacsabackend.shop.dto.ShopResponse;

import java.util.List;

public interface ShopService {
    List<ShopDto> search(String query, String x, String y);
}
