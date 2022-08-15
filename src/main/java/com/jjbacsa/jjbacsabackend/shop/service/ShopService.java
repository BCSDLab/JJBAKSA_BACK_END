package com.jjbacsa.jjbacsabackend.shop.service;

import com.jjbacsa.jjbacsabackend.shop.dto.ShopResponse;
import org.json.simple.parser.ParseException;

public interface ShopService {
    ShopResponse getShop(String placeId) throws ParseException;
    ShopResponse searchShop(String keyword) throws ParseException;

}
