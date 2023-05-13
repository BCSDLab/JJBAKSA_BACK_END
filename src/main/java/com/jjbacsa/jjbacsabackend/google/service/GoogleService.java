package com.jjbacsa.jjbacsabackend.google.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jjbacsa.jjbacsabackend.google.dto.ShopApiDto;
import com.jjbacsa.jjbacsabackend.google.dto.SimpleShopDto;
import com.jjbacsa.jjbacsabackend.google.response.ShopQueryResponses;
import com.jjbacsa.jjbacsabackend.google.response.ShopResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GoogleService {

    //쿼리 다중 검색
    ShopQueryResponses searchShopQuery(String query, double x, double y) throws JsonProcessingException;

    //상점 쿼리 다중 검색에서 다음 페이지
    ShopQueryResponses searchShopQueryNext(String pageToken, double x, double y) throws JsonProcessingException;

    //상점 상세정보
    ShopResponse getShopDetails(String placeId, double x, double y) throws JsonProcessingException;

    //상점 저장
    void saveGoogleShop(String placeId);

    //가까운 음식점
    List<SimpleShopDto> getNearByShops(double x, double y, double radius);

    //친구 음식점
    List<SimpleShopDto> getFriendsShops(double x, double y, double radius);

    //북마크 음식점
    List<SimpleShopDto> getBookMarkShops(double x, double y, double radius);

    //메인페이지

    //상점 세부페이지

}
