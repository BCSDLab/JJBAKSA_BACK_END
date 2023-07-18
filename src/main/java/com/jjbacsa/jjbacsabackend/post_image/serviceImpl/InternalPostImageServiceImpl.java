package com.jjbacsa.jjbacsabackend.post_image.serviceImpl;

import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.image.service.InternalImageService;
import com.jjbacsa.jjbacsabackend.post.entity.PostEntity;
import com.jjbacsa.jjbacsabackend.post_image.entity.PostImageEntity;
import com.jjbacsa.jjbacsabackend.post_image.repository.PostImageRepository;
import com.jjbacsa.jjbacsabackend.post_image.service.InternalPostImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InternalPostImageServiceImpl implements InternalPostImageService {

    @Value("${cloud.aws.s3.post}")
    private String postPath;
    @Value("${cloud.aws.cloudfront.post-url-format}")
    private String postUrlFormat;

    private final InternalImageService imageService;
    private final PostImageRepository postImageRepository;

    @Override
    public List<PostImageEntity> create(List<MultipartFile> images) throws IOException {
        List<ImageEntity> imageEntities = imageService.createImages(images, postPath, postUrlFormat);
        List<PostImageEntity> postImageEntities = new ArrayList<>();
        for (ImageEntity image : imageEntities) {
            PostImageEntity postImage = PostImageEntity.builder()
                    .image(image)
                    .build();
            postImageEntities.add(postImage);
        }
        return postImageEntities;
    }

    @Override
    public void modify(List<MultipartFile> images, PostEntity postEntity) throws IOException {
        List<PostImageEntity> postImages = postEntity.getPostImages();
        List<ImageEntity> modifyImages = imageService.modifyImages(
                images,
                postImages.stream().map(PostImageEntity::getImage).collect(Collectors.toList()),
                postPath,
                postUrlFormat);
        for (int i = 0; i < Math.max(modifyImages.size(), postImages.size()); i++) {
            ImageEntity newImage = i < modifyImages.size() ? modifyImages.get(i) : null;
            PostImageEntity postImage = i < postImages.size() ? postImages.get(i) : null;
            if (newImage == null) { // 기존 이미지 삭제
                delete(postImage);
                postImages.remove(i);
            } else if (postImage == null) { // 새로 추가된 이미지
                postEntity.addPostImageEntity(PostImageEntity.builder()
                        .image(newImage)
                        .build());
            } else {    // 이미지 변경
                postImage.setImage(newImage);
            }
        }
    }

    @Override
    public void delete(PostImageEntity postImageEntity) {

        imageService.deleteImage(postImageEntity.getImage().getId());

        postImageRepository.deleteById(postImageEntity.getId());
    }
}
