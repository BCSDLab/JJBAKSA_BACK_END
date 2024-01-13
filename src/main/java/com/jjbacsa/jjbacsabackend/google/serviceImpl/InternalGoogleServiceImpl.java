package com.jjbacsa.jjbacsabackend.google.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.google.repository.GoogleShopRepository;
import com.jjbacsa.jjbacsabackend.google.service.InternalGoogleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InternalGoogleServiceImpl implements InternalGoogleService {

    private final GoogleShopRepository googleShopRepository;

    @Override
    @Transactional(readOnly = true)
    public GoogleShopEntity getGoogleShopById(Long shopId) {

        return googleShopRepository.findById(shopId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.SHOP_NOT_EXISTS_EXCEPTION));
    }

    @Override
    public Long getShopIdByPlaceId(String placeId) {
        return googleShopRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.SHOP_NOT_EXISTS_EXCEPTION))
                .getId();
    }

    @Override
    public void addTotalRating(Long shopId, int delta) {

        GoogleShopEntity shop = getGoogleShopById(shopId);
        shop.getShopCount().setTotalRating(googleShopRepository.getTotalRating(shopId) + delta);
    }

    @Override
    public void increaseRatingCount(Long shopId) {

        GoogleShopEntity shop = getGoogleShopById(shopId);
        shop.getShopCount().setRatingCount(googleShopRepository.getRatingCount(shopId) + 1);
    }

    @Override
    public void decreaseRatingCount(Long shopId) {

        GoogleShopEntity shop = getGoogleShopById(shopId);
        shop.getShopCount().setRatingCount(googleShopRepository.getRatingCount(shopId) - 1);
    }

}
