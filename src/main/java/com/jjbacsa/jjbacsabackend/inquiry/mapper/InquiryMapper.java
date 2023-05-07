package com.jjbacsa.jjbacsabackend.inquiry.mapper;

import com.jjbacsa.jjbacsabackend.inquiry.dto.request.InquiryRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.response.InquiryResponse;
import com.jjbacsa.jjbacsabackend.inquiry.entity.InquiryEntity;
import com.jjbacsa.jjbacsabackend.inquiry_image.dto.response.InquiryImageResponse;
import com.jjbacsa.jjbacsabackend.inquiry_image.entity.InquiryImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface InquiryMapper {
    InquiryMapper INSTANCE = Mappers.getMapper(InquiryMapper.class);

    InquiryEntity toInquiryEntity(InquiryRequest inquiryRequest);

    @Mappings({
            @Mapping(source = "image.originalName", target = "originalName"),
            @Mapping(source = "image.path", target = "path"),
            @Mapping(source = "image.url", target = "imageUrl")
    })
    InquiryImageResponse toInquiryImageResponse(InquiryImageEntity inquiryImageEntity);

    InquiryResponse toInquiryResponse(InquiryEntity inquiryEntity);
}
