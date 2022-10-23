package com.jjbacsa.jjbacsabackend.user.service;

import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import org.springframework.web.multipart.MultipartFile;

public interface InternalProfileService {
    ImageEntity updateProfileImage(MultipartFile profile) throws Exception;

    void deleteProfileImage(ImageEntity profile) throws Exception;
}
