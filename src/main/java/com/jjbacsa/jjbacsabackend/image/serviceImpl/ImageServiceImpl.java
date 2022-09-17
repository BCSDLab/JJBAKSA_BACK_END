package com.jjbacsa.jjbacsabackend.image.serviceImpl;

import com.jjbacsa.jjbacsabackend.image.dto.request.ImageRequest;
import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.image.mapper.ImageMapper;
import com.jjbacsa.jjbacsabackend.image.repository.ImageRepository;
import com.jjbacsa.jjbacsabackend.image.service.ImageService;
import com.jjbacsa.jjbacsabackend.review.entity.ReviewEntity;
import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import com.jjbacsa.jjbacsabackend.review_image.repository.ReviewImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService {
    @Value("${image.review.path}")
    private String reviewPath;
    private final ImageRepository imageRepository;
    private final ReviewImageRepository reviewImageRepository;

    // TODO: AWS S3에 저장하는 방식으로 변경
    @Override
    public List<ReviewImageEntity> createReviewImages(List<MultipartFile> images) throws IOException {
        List<ReviewImageEntity> result = new ArrayList<>();

        for(MultipartFile image: images){
            // 파일의 확장자 추출
            String originalName = image.getOriginalFilename();
            String imagePath = createReviewFile(image);

            ImageEntity imageEntity = ImageMapper.INSTANCE.toImageEntity(new ImageRequest(imagePath, originalName));
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
        String filePath;

        if(origin_size<= new_size) {   // 업데이트된 이미지가 기존과 같거나 많을 때
            for (int i = 0; i < origin_size; i++) {
                MultipartFile image = images.get(i);
                String originalName = image.getOriginalFilename();
                filePath = createReviewFile(image);

                ImageEntity imageEntity = reviewEntity.getReviewImages().get(i).getImage();
                deleteReviewImage(imageEntity);     // 기존 파일 삭제
                imageEntity.updateImage(filePath, originalName);
                reviewEntity.getReviewImages().get(i).setImage(imageEntity);
            }
            for(int i=origin_size; i<new_size; i++){    // 추가된 이미지 넣기
                MultipartFile image = images.get(i);
                String originalName = image.getOriginalFilename();
                filePath= createReviewFile(image);

                ImageEntity imageEntity = ImageMapper.INSTANCE.toImageEntity(new ImageRequest(filePath, originalName));
                ReviewImageEntity reviewImageEntity = ReviewImageEntity.builder()
                        .review(reviewEntity)
                        .image(imageEntity)
                        .build();
                reviewImageRepository.save(reviewImageEntity);
                reviewEntity.getReviewImages().add(reviewImageEntity);
            }
        }
        else{   // 업데이트 된 images가 기존보다 더 적을 때
            for (int i = 0; i < new_size; i++) {
                MultipartFile image = images.get(i);
                String originalName = image.getOriginalFilename();
                filePath = createReviewFile(image);

                ImageEntity imageEntity = reviewEntity.getReviewImages().get(i).getImage();
                deleteReviewImage(imageEntity);
                imageEntity.updateImage(filePath, originalName);
                reviewEntity.getReviewImages().get(i).setImage(imageEntity);

            }
            for(int i=new_size; i<origin_size; i++){
                reviewImageRepository.deleteById(reviewImageEntities.get(i).getId());
                reviewEntity.getReviewImages().remove(i);
            }
        }
        return reviewEntity;
    }

    private void deleteReviewImage(ImageEntity imageEntity) {
        File im = new File(imageEntity.getPath());
        if(!im.delete()) throw new RuntimeException("존재하지 않는 이미지입니다. : " + imageEntity.getPath());
    }

    private String createReviewFile(MultipartFile image) throws IOException {
        String contentType = image.getContentType();
        String originalFileExtension;
        // 확장자명이 존재하지 않을 경우 처리 x
        if(ObjectUtils.isEmpty(contentType)) {
            return null;
        }
        else {  // 확장자가 jpeg, png인 파일들만 받아서 처리
            if(contentType.contains("image/jpeg") || contentType.contains("image/jpg"))
                originalFileExtension = ".jpg";
            else if(contentType.contains("image/png"))
                originalFileExtension = ".png";
            else throw new RuntimeException("올바르지 못한 형식의 이미지 파일입니다. : "+contentType);
        }
        // 파일명 중복 피하고자 나노초까지 얻어와 지정, yml에 설정한 root directory 아래 review 폴더에 저장되도록 함
        String imagePath = reviewPath + System.nanoTime() + originalFileExtension;
        File dest = new File(imagePath);
        image.transferTo(dest);
        return dest.getPath();
    }

}
