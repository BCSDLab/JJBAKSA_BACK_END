package com.jjbacsa.jjbacsabackend.google.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.ApiException;
import com.jjbacsa.jjbacsabackend.etc.exception.BaseException;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopScrapResponse;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.google.repository.GoogleShopRepository;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.google.service.GoogleShopService;
import com.jjbacsa.jjbacsabackend.google.service.InternalGoogleService;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InternalGoogleServiceImpl implements InternalGoogleService {

    private final GoogleShopRepository googleShopRepository;
    private final GoogleShopService googleService;

    @Override
    public GoogleShopEntity getGoogleShopByPlaceId(String placeId) {

        return googleShopRepository.findByPlaceId(placeId)
                .orElseGet(() -> {
                    try {
                        return saveGoogleShop(placeId);
                    } catch (Exception e) {
                        throw new ApiException(ErrorMessage.NOT_FOUND_EXCEPTION);
                    }
                });
    }

    @Override
    @Transactional(readOnly = true)
    public GoogleShopEntity getGoogleShopById(Long shopId) {

        return googleShopRepository.findById(shopId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.SHOP_NOT_EXISTS_EXCEPTION));
    }

    @Override
    @Transactional(readOnly = true)
    public ShopResponse getShopDetails(String placeId) throws Exception {
        return googleService.getShopDetails(placeId, true);
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

    private GoogleShopEntity saveGoogleShop(String placeId) throws Exception {
        ShopResponse shopResponse = googleService.getShopDetails(placeId, true);
        GoogleShopEntity googleShopEntity = GoogleShopEntity.builder()
                .placeId(shopResponse.getPlaceId())
                .build();

        return googleShopRepository.save(googleShopEntity);
    }

    @Override
    public ShopScrapResponse formattedToShopResponse(ScrapEntity scrap) throws Exception {
        ShopScrapResponse shopScrapResponse = this.googleService.getShopScrap(scrap.getShop().getPlaceId(), scrap.getId());
        return shopScrapResponse;
    }
}
