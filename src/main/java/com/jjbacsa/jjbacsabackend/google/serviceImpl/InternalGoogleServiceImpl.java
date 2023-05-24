package com.jjbacsa.jjbacsabackend.google.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.google.entity.GoogleShopEntity;
import com.jjbacsa.jjbacsabackend.google.repository.GoogleShopRepository;
import com.jjbacsa.jjbacsabackend.google.service.InternalGoogleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class InternalGoogleServiceImpl implements InternalGoogleService {

    private final GoogleShopRepository googleShopRepository;

    @Transactional(readOnly = true)
    @Override
    public GoogleShopEntity getGoogleShopById(Long id) {
        return googleShopRepository.findById(id)
                .orElseThrow(()-> new RequestInputException((ErrorMessage.SHOP_NOT_EXISTS_EXCEPTION)));
    }

    @Transactional
    @Override
    public GoogleShopEntity saveGoogleShop(String placeId) {
        GoogleShopEntity googleShopEntity = GoogleShopEntity.builder()
                .placeId(placeId)
                .build();

        return this.googleShopRepository.save(googleShopEntity);
    }
}
