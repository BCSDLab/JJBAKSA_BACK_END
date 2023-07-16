package com.jjbacsa.jjbacsabackend.google.service;

import com.jjbacsa.jjbacsabackend.google.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopScrapResponse;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;

public interface InternalGoogleApiService {

    GoogleShopEntity getGoogleShopByPlaceId(String placeId);

    ShopResponse getShopDetails(String placeId) throws Exception;

    ShopScrapResponse formattedToShopResponse(ScrapEntity scrap) throws Exception;

}
