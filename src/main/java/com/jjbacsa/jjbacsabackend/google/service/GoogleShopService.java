package com.jjbacsa.jjbacsabackend.google.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jjbacsa.jjbacsabackend.google.dto.SimpleShopDto;
import com.jjbacsa.jjbacsabackend.google.dto.request.ShopRequest;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopQueryResponses;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopScrapResponse;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopSimpleResponse;

import java.util.List;

public interface GoogleShopService {

    //구글 상점 쿼리 다중 검색
    ShopQueryResponses searchShopQuery(String query, ShopRequest shopRequest) throws JsonProcessingException;

    //구글 상점 쿼리 다중 검색에서 다음 페이지
    ShopQueryResponses searchShopQueryNext(String pageToken, ShopRequest shopRequest) throws JsonProcessingException;

    //구글 상점 상세정보
    ShopResponse getShopDetails(String placeId, boolean isDetail) throws Exception;

    //Scrap 상점 반환(스크랩한 상점 반환 시에 사용)
    ShopScrapResponse getShopScrap(String placeId, Long scrapId) throws JsonProcessingException;

    //메인페이지
    List<ShopSimpleResponse> getShops(Integer nearBy, Integer friend, Integer scrap, ShopRequest shopRequest) throws Exception;
}
