package com.jjbacsa.jjbacsabackend.image.service;

import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface InternalImageService {

    List<ImageEntity> createImages(List<MultipartFile> images, String path, String urlFormat) throws IOException;
    List<ImageEntity> modifyImages(List<MultipartFile> images, List<ImageEntity>  imageEntities, String path, String urlFormat) throws IOException;
    void deleteImage(Long imageId);
}
