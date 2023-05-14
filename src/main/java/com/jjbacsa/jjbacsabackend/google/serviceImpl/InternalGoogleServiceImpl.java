package com.jjbacsa.jjbacsabackend.google.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.google.repository.GoogleShopRepository;
import com.jjbacsa.jjbacsabackend.google.service.InternalGoogleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class InternalGoogleServiceImpl implements InternalGoogleService {

    private final GoogleShopRepository googleShopRepository;

    @Override
    public GoogleShopEntity getGoogleShopById(Long id) {
        return googleShopRepository.findById(id)
                .orElseThrow(()-> new RequestInputException((ErrorMessage.SHOP_NOT_EXISTS_EXCEPTION)));
    }

    @Override
    public GoogleShopEntity saveGoogleShop(String placeId) {
        GoogleShopEntity googleShopEntity = GoogleShopEntity.builder()
                .placeId(placeId)
                .build();

        return this.googleShopRepository.save(googleShopEntity);
    }
}
