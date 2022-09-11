package com.jjbacsa.jjbacsabackend.shop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jjbacsa.jjbacsabackend.shop.dto.ShopRequest;
import com.jjbacsa.jjbacsabackend.shop.dto.ShopResponse;
import com.jjbacsa.jjbacsabackend.shop.dto.ShopSummary;
import org.springframework.data.domain.Page;

public interface ShopService {
    //단건 조회
    ShopResponse getShop(String placeId) throws JsonProcessingException;

    //DB 내 상점 검색
    Page<ShopSummary> searchShop(ShopRequest shopRequest);
}