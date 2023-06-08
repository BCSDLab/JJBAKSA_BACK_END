package com.jjbacsa.jjbacsabackend.inquiry_image.service;

import com.jjbacsa.jjbacsabackend.inquiry.entity.InquiryEntity;
import com.jjbacsa.jjbacsabackend.inquiry_image.entity.InquiryImageEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface InternalInquiryImageService {
    void modify(List<MultipartFile> images, InquiryEntity inquiryEntity) throws IOException;

    List<InquiryImageEntity> create(List<MultipartFile> inquiryImages) throws IOException;

    void delete(InquiryImageEntity inquiryImageEntity);

}
