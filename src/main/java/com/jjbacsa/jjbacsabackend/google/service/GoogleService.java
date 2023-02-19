package com.jjbacsa.jjbacsabackend.google.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jjbacsa.jjbacsabackend.google.response.ShopQueryResponses;
import com.jjbacsa.jjbacsabackend.google.response.ShopResponse;

public interface GoogleService {

    //쿼리 다중 검색
    ShopQueryResponses searchShopQuery(String query,String type,double x, double y) throws JsonProcessingException;

    //상점 쿼리 다중 검색에서 다음 페이지
    ShopQueryResponses searchShopQueryNext(String pageToken, double x, double y) throws JsonProcessingException;

    //상점 상세정보
    ShopResponse getShopDetails(String placeId, double x, double y) throws JsonProcessingException;

    //상점 저장
    void saveGoogleShop(String placeId);

    //반경에 따른 상점 쿼리 검색
    ShopQueryResponses searchShopQueryWithRadius(String query,String type,double x, double y, double radius) throws JsonProcessingException;

    //상점 사진 가져오는 url 반환
    String getPhotoUrl(String token);
}
