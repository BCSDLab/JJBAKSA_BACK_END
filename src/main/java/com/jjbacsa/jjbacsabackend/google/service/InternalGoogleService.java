package com.jjbacsa.jjbacsabackend.google.service;

import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;

public interface InternalGoogleService {

    GoogleShopEntity getGoogleShopById(Long shopId);

    void addTotalRating(Long shopId, int delta);

    void increaseRatingCount(Long shopId);

    void decreaseRatingCount(Long shopId);

}
