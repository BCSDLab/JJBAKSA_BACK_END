package com.jjbacsa.jjbacsabackend.shop.service;

import com.jjbacsa.jjbacsabackend.shop.dto.Shop;
import org.json.simple.parser.ParseException;

public interface ShopService {
    Long getShop(String placeId) throws ParseException;
    Long searchShop(String keyword) throws ParseException;
    Shop getShopDetails(String placeId) throws ParseException;
    Long register(Shop shop);
}
