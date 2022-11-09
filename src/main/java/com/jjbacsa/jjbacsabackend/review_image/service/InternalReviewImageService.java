package com.jjbacsa.jjbacsabackend.review_image.service;

import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface InternalReviewImageService {
    void modifyReviewImages(List<MultipartFile> images, ReviewEntity reviewEntity) throws IOException;
    List<ReviewImageEntity> createReviewImages(List<MultipartFile> ReviewImages) throws IOException;
    void delete(ReviewImageEntity reviewImageEntity);
}
