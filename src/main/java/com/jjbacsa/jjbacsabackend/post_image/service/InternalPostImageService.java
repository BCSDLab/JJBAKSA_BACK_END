package com.jjbacsa.jjbacsabackend.post_image.service;

import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import com.jjbacsa.jjbacsabackend.post_image.entity.PostImageEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface InternalPostImageService {
    List<PostImageEntity> create(List<MultipartFile> images) throws IOException;

    void modify(List<MultipartFile> images, PostEntity postEntity) throws IOException;

    void delete(PostImageEntity postImageEntity);
}
