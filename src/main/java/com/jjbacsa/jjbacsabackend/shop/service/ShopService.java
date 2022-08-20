package com.jjbacsa.jjbacsabackend.shop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jjbacsa.jjbacsabackend.shop.dto.ShopResponse;
import org.json.simple.parser.ParseException;

public interface ShopService {
    ShopResponse getShop(String placeId) throws JsonProcessingException;
    ShopResponse searchShop(String keyword) throws ParseException;
}
