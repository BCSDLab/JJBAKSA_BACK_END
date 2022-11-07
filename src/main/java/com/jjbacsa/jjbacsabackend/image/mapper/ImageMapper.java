package com.jjbacsa.jjbacsabackend.image.mapper;

import com.jjbacsa.jjbacsabackend.image.dto.request.ImageRequest;
import com.jjbacsa.jjbacsabackend.image.dto.response.ImageResponse;
import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface ImageMapper {
    com.jjbacsa.jjbacsabackend.image.mapper.ImageMapper INSTANCE = Mappers.getMapper(com.jjbacsa.jjbacsabackend.image.mapper.ImageMapper.class);

    @Mapping(source = "imageUrl", target="url")
    ImageEntity toImageEntity(ImageRequest imageRequest);

    ImageResponse toImageResponse(ImageEntity imageEntity);

}
