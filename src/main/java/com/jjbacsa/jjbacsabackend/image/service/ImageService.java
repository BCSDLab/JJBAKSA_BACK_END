package com.jjbacsa.jjbacsabackend.image.service;

import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    List<ReviewImageEntity> createReviewImages(List<MultipartFile> images) throws IOException;
    ReviewEntity modifyReviewImages(List<MultipartFile> images, ReviewEntity reviewEntity) throws IOException;
    void deleteImage(Long imageId);
}
