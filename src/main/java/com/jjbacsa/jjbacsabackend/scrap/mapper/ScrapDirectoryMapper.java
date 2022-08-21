package com.jjbacsa.jjbacsabackend.scrap.mapper;

import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapDirectoryResponse;
import com.jjbacsa.jjbacsabackend.scrap.entity.ScrapDirectoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface ScrapDirectoryMapper {

    ScrapDirectoryMapper INSTANCE = Mappers.getMapper(ScrapDirectoryMapper.class);

    @Mapping(source = "scrapCount", target = "scrapDirectoryCount.scrapCount")
    ScrapDirectoryResponse toScrapDirectoryResponse(ScrapDirectoryEntity directory);
}
