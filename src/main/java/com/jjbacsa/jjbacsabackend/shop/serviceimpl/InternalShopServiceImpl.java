package com.jjbacsa.jjbacsabackend.shop.serviceimpl;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import com.jjbacsa.jjbacsabackend.shop.repository.ShopRepository;
import com.jjbacsa.jjbacsabackend.shop.service.InternalShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InternalShopServiceImpl implements InternalShopService {

    private final ShopRepository shopRepository;

    @Override
    public ShopEntity getShopById(Long shopId) {

        return shopRepository.findById(shopId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.SHOP_NOT_EXISTS_EXCEPTION));
    }

    @Override
    @Transactional
    public void addTotalRating(Long shopId, int delta) {

        ShopEntity shop = getShopById(shopId);
        shop.getShopCount().setTotalRating(shopRepository.getTotalRating(shopId) + delta);
    }

    @Override
    @Transactional
    public void increaseRatingCount(Long shopId) {

        ShopEntity shop = getShopById(shopId);
        shop.getShopCount().setRatingCount(shopRepository.getRatingCount(shopId) + 1);
    }

    @Override
    @Transactional
    public void decreaseRatingCount(Long shopId) {

        ShopEntity shop = getShopById(shopId);
        shop.getShopCount().setRatingCount(shopRepository.getRatingCount(shopId) - 1);
    }
}
