package com.jjbacsa.jjbacsabackend.shop.service;

import com.jjbacsa.jjbacsabackend.shop.dto.request.ShopRequest;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopSummaryResponse;
import com.jjbacsa.jjbacsabackend.search.dto.TrendingResponse;
import org.springframework.data.domain.Page;

public interface ShopService {
    //단건 조회
    ShopResponse getShop(String placeId);

    //DB 내 상점 검색
    Page<ShopSummaryResponse> searchShop(ShopRequest shopRequest, Integer page, Integer size);
}