package com.jjbacsa.jjbacsabackend.shop.service;

import com.jjbacsa.jjbacsabackend.shop.dto.request.ShopRequest;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopSummaryResponse;
import com.jjbacsa.jjbacsabackend.shop.dto.response.TrendingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShopService {
    //단건 조회
    ShopResponse getShop(String placeId);

    //DB 내 상점 검색
    Page<ShopSummaryResponse> searchShop(ShopRequest shopRequest, Pageable pageable);

    //인기검색어
    TrendingResponse getTrending();
}