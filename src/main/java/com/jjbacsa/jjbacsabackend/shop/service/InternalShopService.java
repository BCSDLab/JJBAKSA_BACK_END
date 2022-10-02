package com.jjbacsa.jjbacsabackend.shop.service;

import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;

public interface InternalShopService {

    ShopEntity getShopById(Long shopId);

    void addTotalRating(Long shopId, int delta);

    void increaseRatingCount(Long shopId);

    void decreaseRatingCount(Long shopId);
}
