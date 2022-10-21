package com.jjbacsa.jjbacsabackend.review_image.serviceImpl;

import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.image.service.InternalImageService;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import com.jjbacsa.jjbacsabackend.review_image.repository.ReviewImageRepository;
import com.jjbacsa.jjbacsabackend.review_image.service.InternalReviewImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InternalReviewImageServiceImpl implements InternalReviewImageService {
    @Value("${cloud.aws.s3.review}")
    private String reviewPath;
    @Value("${cloud.aws.cloudfront.review-url-format}")
    private String reviewUrlFormat;

    private final InternalImageService imageService;
    private final ReviewImageRepository reviewImageRepository;

    @Override
    public void modifyReviewImages(List<MultipartFile> images, ReviewEntity reviewEntity) throws IOException {
        List<ReviewImageEntity> reviewImageEntities = reviewEntity.getReviewImages();
        List<ImageEntity> imageEntities = new ArrayList<>();
        for(ReviewImageEntity reviewImage : reviewImageEntities){
            imageEntities.add(reviewImage.getImage());
        }
        List<ImageEntity> modifyImageEntities = imageService.modifyImages(images, imageEntities, reviewPath, reviewUrlFormat);  // 이미지를 변경하고 새로 받아서
        int origin_size = imageEntities.size();
        int new_size = modifyImageEntities.size();
        if(origin_size<= new_size) {   // 업데이트된 이미지가 기존과 같거나 많을 때
            for (int i = 0; i < origin_size; i++) {
                reviewImageEntities.get(i).setImage(modifyImageEntities.get(i));
            }
            for(int i=origin_size; i<new_size; i++){    // 추가된 이미지 넣기
                ReviewImageEntity reviewImageEntity = new ReviewImageEntity();
                reviewImageEntity.setImage(modifyImageEntities.get(i));
                reviewEntity.addReviewImageEntity(reviewImageEntity);
            }
        }
        else{   // 업데이트 된 images가 기존보다 더 적을 때
            for (int i = 0; i < new_size; i++) {
                reviewImageEntities.get(i).setImage(modifyImageEntities.get(i));
            }
            for(int i=new_size; i<origin_size; i++){
                delete(reviewImageEntities.get(i));
                reviewImageEntities.remove(i);
            }
        }
    }

    @Override
    public List<ReviewImageEntity> createReviewImages(List<MultipartFile> reviewImages) throws IOException {
        List<ImageEntity> imageEntities = imageService.createImages(reviewImages, reviewPath, reviewUrlFormat);
        List<ReviewImageEntity> reviewImageEntities = new ArrayList<>();
        for(ImageEntity image: imageEntities){
            ReviewImageEntity reviewImageEntity = new ReviewImageEntity();
            reviewImageEntity.setImage(image);
            reviewImageEntities.add(reviewImageEntity);
        }
        return reviewImageEntities;
    }

    @Override
    public void delete(ReviewImageEntity reviewImageEntity) {
        imageService.deleteImage(reviewImageEntity.getImage().getId());
        reviewImageRepository.deleteById(reviewImageEntity.getId());
    }
}
