package com.jjbacsa.jjbacsabackend.shop.mapper;

import com.jjbacsa.jjbacsabackend.shop.dto.ShopDto;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopResponse;
import com.jjbacsa.jjbacsabackend.shop.dto.response.ShopSummaryResponse;
import com.jjbacsa.jjbacsabackend.shop.entity.ShopEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface ShopMapper {

    ShopMapper INSTANCE = Mappers.getMapper(ShopMapper.class);

    ShopEntity toEntity(ShopDto shopDto);

    @Mapping(source="id",target="shopId")
    ShopResponse toShopResponse(ShopEntity shopEntity);

    @Mapping(source="id", target = "shopId")
    ShopSummaryResponse toShopSummaryResponse(ShopEntity shopEntity);
}
