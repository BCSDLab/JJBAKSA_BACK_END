package com.jjbacsa.jjbacsabackend.google.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.ApiException;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopPinResponse;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.google.dto.response.ShopScrapResponse;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.google.repository.GoogleShopRepository;
import com.jjbacsa.jjbacsabackend.google.service.GoogleShopService;
import com.jjbacsa.jjbacsabackend.google.service.InternalGoogleApiService;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InternalGoogleApiServiceImpl implements InternalGoogleApiService {

    private final GoogleShopRepository googleShopRepository;
    private final GoogleShopService googleShopService;

    @Override
    public GoogleShopEntity getGoogleShopByPlaceId(String placeId) {

        return googleShopRepository.findByPlaceId(placeId)
                .orElseGet(() -> {
                    try {
                        return saveGoogleShop(placeId);
                    } catch (Exception e) {
                        throw new ApiException(ErrorMessage.INVALID_REQUEST_EXCEPTION);
                    }
                });
    }

    @Override
    public ShopResponse getShopDetails(String placeId) throws Exception {
        return googleShopService.getShopDetails(placeId);
    }

    @Override
    public ShopScrapResponse formattedToShopResponse(ScrapEntity scrap) throws Exception {
        ShopScrapResponse shopScrapResponse = googleShopService.getShopScrap(scrap.getShop().getPlaceId());

        return shopScrapResponse;
    }

    private GoogleShopEntity saveGoogleShop(String placeId) throws Exception {
        if (googleShopService.isShopExist(placeId) == false) {
            throw new ApiException(ErrorMessage.INVALID_REQUEST_EXCEPTION);
        }

        GoogleShopEntity googleShopEntity = GoogleShopEntity.builder()
                .placeId(placeId)
                .build();

        return googleShopRepository.save(googleShopEntity);
    }
}
