package com.jjbacsa.jjbacsabackend.image.serviceImpl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.image.dto.request.ImageRequest;
import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.image.mapper.ImageMapper;
import com.jjbacsa.jjbacsabackend.image.repository.ImageRepository;
import com.jjbacsa.jjbacsabackend.image.service.ImageService;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import com.jjbacsa.jjbacsabackend.review_image.repository.ReviewImageRepository;
import com.jjbacsa.jjbacsabackend.review_image.service.InternalReviewImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService {
    @Value("${cloud.aws.s3.review}")
    private String reviewPath;
    @Value("${cloud.aws.cloudfront.review-url-format}")
    private String reviewUrlFormat;

    private Long FILE_MAX_SIZE = 10000000L;

    // TODO: InternalService로 변경
    private final ReviewImageRepository reviewImageRepository;
    private final ImageRepository imageRepository;
    private final AmazonS3Client amazonS3;

    // TODO: 코드 중복 리팩터링
    @Override
    public List<ReviewImageEntity> createReviewImages(List<MultipartFile> images) throws IOException {
        List<ReviewImageEntity> result = new ArrayList<>();

        for(MultipartFile image: images){
            // 파일의 확장자 추출
            String originalName = image.getOriginalFilename();
            String fileName = createReviewFile(image);
            String imagePath = reviewPath.concat(fileName);
            String imageUrl = reviewUrlFormat.concat(fileName);
            ImageEntity imageEntity = ImageMapper.INSTANCE.toImageEntity(new ImageRequest(imagePath, originalName, imageUrl));
            ReviewImageEntity reviewImageEntity = new ReviewImageEntity();
            reviewImageEntity.setImage(imageEntity);
            imageRepository.save(imageEntity);
            result.add(reviewImageEntity);
        }
        return result;
    }

    @Override
    public ReviewEntity modifyReviewImages(List<MultipartFile> images, ReviewEntity reviewEntity) throws IOException {
        List<ReviewImageEntity> reviewImageEntities = reviewEntity.getReviewImages();
        int origin_size = reviewEntity.getReviewImages().size();
        int new_size = images.size();
        log.info(new_size +", "+origin_size);
        if(origin_size<= new_size) {   // 업데이트된 이미지가 기존과 같거나 많을 때
            for (int i = 0; i < origin_size; i++) {
                MultipartFile image = images.get(i);
                String originalName = image.getOriginalFilename();
                String fileName = createReviewFile(image);
                String imagePath = reviewPath.concat(fileName);
                String imageUrl = reviewUrlFormat.concat(fileName);

                ImageEntity imageEntity = reviewEntity.getReviewImages().get(i).getImage();
                deleteImage(imageEntity);     // 기존 파일 삭제
                imageEntity.updateImage(imagePath, originalName, imageUrl);
                reviewEntity.getReviewImages().get(i).setImage(imageEntity);
            }
            for(int i=origin_size; i<new_size; i++){    // 추가된 이미지 넣기
                MultipartFile image = images.get(i);
                String originalName = image.getOriginalFilename();
                String fileName = createReviewFile(image);
                String imagePath = reviewPath.concat(fileName);
                String imageUrl = reviewUrlFormat.concat(fileName);

                ImageEntity imageEntity = ImageMapper.INSTANCE.toImageEntity(new ImageRequest(imagePath, originalName, imageUrl));
                ReviewImageEntity reviewImageEntity = ReviewImageEntity.builder()
                        .review(reviewEntity)
                        .image(imageEntity)
                        .build();
                reviewImageRepository.save(reviewImageEntity);
                reviewEntity.addReviewImageEntity(reviewImageEntity);
            }
        }
        else{   // 업데이트 된 images가 기존보다 더 적을 때
            for (int i = 0; i < new_size; i++) {
                MultipartFile image = images.get(i);
                String originalName = image.getOriginalFilename();
                String fileName = createReviewFile(image);
                String imagePath = reviewPath.concat(fileName);
                String imageUrl = reviewUrlFormat.concat(fileName);

                ImageEntity imageEntity = reviewEntity.getReviewImages().get(i).getImage();
                deleteImage(imageEntity);
                imageEntity.updateImage(imagePath, originalName, imageUrl);
                reviewEntity.getReviewImages().get(i).setImage(imageEntity);

            }
            for(int i=new_size; i<origin_size; i++){
                ImageEntity imageEntity = reviewImageEntities.get(i).getImage();
                deleteImage(imageEntity);
                reviewImageRepository.deleteById(reviewImageEntities.get(i).getId());
                reviewEntity.getReviewImages().remove(i);
            }
        }
        return reviewEntity;
    }

    @Override
    public void deleteImage(Long imageId) {
        ImageEntity imageEntity = imageRepository.findById(imageId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.IMAGE_NOT_EXISTS_EXCEPTION));
        deleteImage(imageEntity);
    }
    private void deleteImage(ImageEntity imageEntity) {
        String[] name = imageEntity.getPath().split("\\/");
        String fileName = name[name.length-1];
        String path = reviewPath.substring(0, reviewPath.length() -1);
        log.info(path + fileName);
        amazonS3.deleteObject(new DeleteObjectRequest(path, fileName));
    }

    private String createReviewFile(MultipartFile image) throws IOException {
        String contentType = image.getContentType();
        String originalFileExtension;
        // 확장자가 jpeg, png인 파일들만 받아서 처리
        if(contentType.contains("image/jpeg") || contentType.contains("image/jpg"))
            originalFileExtension = ".jpg";
        else if(contentType.contains("image/png"))
            originalFileExtension = ".png";
        else throw new RequestInputException(ErrorMessage.INVALID_IMAGE);
        if(image.getSize() > FILE_MAX_SIZE) throw new RequestInputException(ErrorMessage.IMAGE_SIZE_OVERFLOW_EXCEPTION);

        // 파일명 중복 피하고자 UUID 랜덤 변수 설정
        String fileName = UUID.randomUUID().toString().concat(originalFileExtension);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        log.info(image.getSize()+"");
        objectMetadata.setContentLength(image.getSize());
        objectMetadata.setContentType(contentType);
        String path = reviewPath.substring(0, reviewPath.length()-1);

        // S3에 저장
        try(InputStream inputStream = image.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(path, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch(IOException e) {
            throw new RequestInputException(ErrorMessage.IMAGE_UPLOAD_FAIL_EXCEPTION);
        }
        return fileName;    // 저장한 이미지 file명 반환
    }

}
