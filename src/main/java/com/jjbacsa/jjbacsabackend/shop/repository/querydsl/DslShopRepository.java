package com.jjbacsa.jjbacsabackend.shop.repository.querydsl;

import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopSummaryResponse;

import java.util.List;

public interface DslShopRepository {

    List<ShopSummaryResponse> search(String keyword, String category);
    List<ShopSummaryResponse> findAllByCategoryName(String categoryName);
    List<ShopSummaryResponse> findByPlaceNameContaining(String keyword);

}
