package com.jjbacsa.jjbacsabackend.image.serviceImpl;


import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.image.dto.request.ImageRequest;
import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.image.mapper.ImageMapper;
import com.jjbacsa.jjbacsabackend.image.repository.ImageRepository;
import com.jjbacsa.jjbacsabackend.image.service.InternalImageService;
import com.jjbacsa.jjbacsabackend.util.AmazonS3Util;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InternalImageServiceImpl implements InternalImageService {

    private Long FILE_MAX_SIZE = 10000000L;

    private final ImageRepository imageRepository;
    private final AmazonS3Util amazonS3;

    @Override
    public List<ImageEntity> createImages(List<MultipartFile> images, String path, String urlFormat) throws IOException {
        List<ImageEntity> result = new ArrayList<>();

        for(MultipartFile image: images){
            ImageForm imageForm = createImageFile(image, path, urlFormat);
            ImageEntity imageEntity = ImageMapper.INSTANCE.toImageEntity(new ImageRequest(imageForm.getImagePath(), imageForm.getOriginalName(), imageForm.getImageUrl()));
            imageRepository.save(imageEntity);
            result.add(imageEntity);
        }
        return result;
    }

    @Override
    public List<ImageEntity> modifyImages(List<MultipartFile> images, List<ImageEntity> imageEntities, String path, String urlFormat) throws IOException {
        List<ImageEntity> modifyImageEntities = new ArrayList<>();
        int origin_size = imageEntities.size();
        int new_size = images.size();
        if(origin_size<= new_size) {   // 업데이트된 이미지가 기존과 같거나 많을 때
            for (int i = 0; i < origin_size; i++) {
                MultipartFile image = images.get(i);
                ImageForm imageForm = createImageFile(image, path, urlFormat);

                ImageEntity imageEntity = imageEntities.get(i);
                deleteImage(imageEntity);     // 기존 파일 삭제
                imageEntity.updateImage(imageForm.getImagePath(), imageForm.getOriginalName(), imageForm.getImageUrl());
                modifyImageEntities.add(imageEntity);
            }
            for(int i=origin_size; i<new_size; i++){    // 추가된 이미지 넣기
                MultipartFile image = images.get(i);
                ImageForm imageForm = createImageFile(image, path, urlFormat);

                ImageEntity imageEntity = ImageMapper.INSTANCE.toImageEntity(new ImageRequest(imageForm.getImagePath(), imageForm.getOriginalName(), imageForm.getImageUrl()));
                imageRepository.save(imageEntity);
                modifyImageEntities.add(imageEntity);
            }
        }
        else{   // 업데이트 된 images가 기존보다 더 적을 때
            for (int i = 0; i < new_size; i++) {
                MultipartFile image = images.get(i);
                ImageForm imageForm = createImageFile(image, path, urlFormat);

                ImageEntity imageEntity = imageEntities.get(i);
                deleteImage(imageEntity);
                imageEntity.updateImage(imageForm.getImagePath(), imageForm.getOriginalName(), imageForm.getImageUrl());
                modifyImageEntities.add(imageEntity);

            }
            for(int i=new_size; i<origin_size; i++){
                ImageEntity imageEntity = imageEntities.get(i);
                deleteImage(imageEntity);
            }
        }
        return modifyImageEntities;
    }

    @Override
    public void deleteImage(Long imageId) {
        ImageEntity imageEntity = imageRepository.findById(imageId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.IMAGE_NOT_EXISTS_EXCEPTION));
        deleteImage(imageEntity);
    }
    private void deleteImage(ImageEntity imageEntity) {
        String[] name = imageEntity.getPath().split("\\/");
        int nameLength = name.length;
        String fileName = name[nameLength-1];
        String[] pathName = IntStream.range(0, name.length-1).filter(idx -> idx != nameLength-1).mapToObj(idx -> name[idx]).toArray(String[]::new);
        String path = String.join("/", pathName);
        amazonS3.deleteImage(path, fileName);
    }

    private ImageForm createImageFile(MultipartFile image, String path, String urlFormat) throws IOException {
        String originalName = image.getOriginalFilename();
        String originalFileExtension = checkImageInfo(image);

        // 파일명 중복 피하고자 UUID 랜덤 변수 설정
        String fileName = UUID.randomUUID().toString().concat(originalFileExtension);
        String filePath = path.substring(0, path.length()-1);
        String imagePath = path.concat(fileName);
        String imageUrl = urlFormat.concat(fileName);
        amazonS3.saveImage(filePath, fileName, image);

        return new ImageForm(originalName, fileName, imagePath, imageUrl);
    }

    private String checkImageInfo(MultipartFile image){
        String contentType = image.getContentType();
        String originalFileExtension;
        // 확장자가 jpeg, png인 파일들만 받아서 처리
        if(contentType.contains("image/jpg"))
            originalFileExtension = ".jpg";
        else if(contentType.contains("image/jpeg"))
            originalFileExtension = ".jpeg";
        else if(contentType.contains("image/png"))
            originalFileExtension = ".png";
        else throw new RequestInputException(ErrorMessage.INVALID_IMAGE);
        if(image.getSize() > FILE_MAX_SIZE) throw new RequestInputException(ErrorMessage.IMAGE_SIZE_OVERFLOW_EXCEPTION);

        return originalFileExtension;
    }

    @RequiredArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    private static class ImageForm{
        String originalName;
        String fileName;
        String imagePath;
        String imageUrl;
    }

}
