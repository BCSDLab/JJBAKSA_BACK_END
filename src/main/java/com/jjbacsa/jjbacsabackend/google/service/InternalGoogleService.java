package com.jjbacsa.jjbacsabackend.google.service;

import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;

public interface InternalGoogleService {
    GoogleShopEntity getGoogleShopById(Long id);

    //리뷰 작성 시에 상점 저장
    GoogleShopEntity saveGoogleShop(String placeId);
}
