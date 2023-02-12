package com.jjbacsa.jjbacsabackend.google.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jjbacsa.jjbacsabackend.google.response.ShopQueryResponses;
import com.jjbacsa.jjbacsabackend.google.response.ShopResponse;

//todo: 반경으로 검색
public interface GoogleService {

    //쿼리 다중 검색
    ShopQueryResponses searchShopQuery(String query,String type,double x, double y) throws JsonProcessingException;

    //상점 쿼리 다중 검색에서 다음 페이지
    ShopQueryResponses searchShopQueryNext(String pageToken, double x, double y) throws JsonProcessingException;

    //상점 상세정보
    ShopResponse getShopDetails(String placeId, double x, double y) throws JsonProcessingException;

    void saveGoogleShop(String placeId);

    //상점 사진 가져오기
//    byte[] getShopPhoto(String photoToken);
}
