package com.jjbacsa.jjbacsabackend.google.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopScrapResponse;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;

import java.util.List;

public interface InternalGoogleService {
    GoogleShopEntity getGoogleShopByPlaceId(String placeId);

    GoogleShopEntity getGoogleShopById(Long shopId);

    ShopResponse getShopDetails(String placeId) throws Exception;

    void addTotalRating(Long shopId, int delta);

    void increaseRatingCount(Long shopId);

    void decreaseRatingCount(Long shopId);

    ShopScrapResponse formattedToShopResponse(ScrapEntity scrap) throws Exception;



}
