package com.jjbacsa.jjbacsabackend.inquiry_image.serviceImpl;

import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.image.service.InternalImageService;
import com.jjbacsa.jjbacsabackend.inquiry.entity.InquiryEntity;
import com.jjbacsa.jjbacsabackend.inquiry_image.entity.InquiryImageEntity;
import com.jjbacsa.jjbacsabackend.inquiry_image.repository.InquiryImageRepository;
import com.jjbacsa.jjbacsabackend.inquiry_image.service.InternalInquiryImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InternalInquiryImageServiceImpl implements InternalInquiryImageService {

    @Value("${cloud.aws.s3.inquiry}")
    private String inquiryPath;
    @Value("${cloud.aws.cloudfront.inquiry-url-format}")
    private String inquiryUrlFormat;

    private final InternalImageService imageService;
    private final InquiryImageRepository inquiryImageRepository;

    @Override
    public void modify(List<MultipartFile> images, InquiryEntity inquiryEntity) throws IOException {
        List<InquiryImageEntity> inquiryImages = inquiryEntity.getInquiryImages();
        List<ImageEntity> modifyImages = imageService.modifyImages(
                images,
                inquiryImages.stream().map(InquiryImageEntity::getImage).collect(Collectors.toList()),
                inquiryPath,
                inquiryUrlFormat);
        for (int i = 0; i < Math.max(modifyImages.size(), inquiryImages.size()); i++) {
            ImageEntity newImage = i < modifyImages.size() ? modifyImages.get(i) : null;
            InquiryImageEntity inquiryImage = i < inquiryImages.size() ? inquiryImages.get(i) : null;
            if (newImage == null) { // 기존 이미지 삭제
                delete(inquiryImage);
                inquiryImages.remove(i);
            } else if (inquiryImage == null) { // 새로 추가된 이미지
                inquiryEntity.addInquiryImageEntity(InquiryImageEntity.builder()
                        .image(newImage)
                        .build());
            } else {    // 이미지 변경
                inquiryImage.setImage(newImage);
            }

        }
    }

    @Override
    public List<InquiryImageEntity> create(List<MultipartFile> inquiryImages) throws IOException {
        List<ImageEntity> imageEntities = imageService.createImages(inquiryImages, inquiryPath, inquiryUrlFormat);
        List<InquiryImageEntity> inquiryImageEntities = new ArrayList<>();
        for (ImageEntity image : imageEntities) {
            InquiryImageEntity inquiryImage = InquiryImageEntity.builder()
                    .image(image)
                    .build();
            inquiryImageEntities.add(inquiryImage);
        }
        return inquiryImageEntities;
    }

    @Override
    public void delete(InquiryImageEntity inquiryImageEntity) {
        imageService.deleteImage(inquiryImageEntity.getImage().getId());
        inquiryImageRepository.delete(inquiryImageEntity);
    }

}
