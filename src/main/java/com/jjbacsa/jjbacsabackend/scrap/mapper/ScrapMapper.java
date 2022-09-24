package com.jjbacsa.jjbacsabackend.scrap.mapper;

import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapResponse;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface ScrapMapper {

    ScrapMapper INSTANCE = Mappers.getMapper(ScrapMapper.class);

    ScrapResponse toScrapResponse(ScrapEntity scrap);
}
