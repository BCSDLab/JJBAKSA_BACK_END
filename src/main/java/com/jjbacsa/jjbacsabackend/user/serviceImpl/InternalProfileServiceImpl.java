package com.jjbacsa.jjbacsabackend.user.serviceImpl;

import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.image.service.InternalImageService;
import com.jjbacsa.jjbacsabackend.user.service.InternalProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Transactional
public class InternalProfileServiceImpl implements InternalProfileService {
    @Value("${cloud.aws.s3.profile}")
    private String profilePath;
    @Value("${cloud.aws.cloudfront.profile-url-format}")
    private String profileUrlFormat;

    private final InternalImageService imageService;

    public ImageEntity updateProfileImage(MultipartFile profile) throws Exception {
        return imageService.createImage(profile, profilePath, profileUrlFormat);
    }

    public void deleteProfileImage(ImageEntity profile) throws Exception {
        imageService.deleteImage(profile.getId());
    }
}
