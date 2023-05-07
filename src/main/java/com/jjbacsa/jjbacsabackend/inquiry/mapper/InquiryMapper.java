package com.jjbacsa.jjbacsabackend.inquiry.mapper;

import com.jjbacsa.jjbacsabackend.inquiry.dto.request.InquiryRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.response.InquiryResponse;
import com.jjbacsa.jjbacsabackend.inquiry.entity.InquiryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "Spring")
public interface InquiryMapper {
    InquiryMapper INSTANCE = Mappers.getMapper(InquiryMapper.class);

    InquiryEntity toInquiryEntity(InquiryRequest inquiryRequest);
    
    @Mapping(source = "writer.nickname", target = "createdBy")
    InquiryResponse toInquiryResponse(InquiryEntity inquiryEntity);
}
