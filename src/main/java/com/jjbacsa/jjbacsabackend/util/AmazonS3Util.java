package com.jjbacsa.jjbacsabackend.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class AmazonS3Util {
    private final AmazonS3Client amazonS3Client;

    public void saveImage(String path, String fileName, MultipartFile image){
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(image.getSize());
        objectMetadata.setContentType(image.getContentType());

        try(InputStream inputStream = image.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(path, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch(IOException e) {
            throw new RequestInputException(ErrorMessage.IMAGE_UPLOAD_FAIL_EXCEPTION);
        }
    }

    public void deleteImage(String path, String fileName){
        amazonS3Client.deleteObject(new DeleteObjectRequest(path, fileName));
    }

}
