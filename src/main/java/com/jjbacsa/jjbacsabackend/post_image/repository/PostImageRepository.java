package com.jjbacsa.jjbacsabackend.post_image.repository;

import com.jjbacsa.jjbacsabackend.post_image.entity.PostImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImageEntity, Long> {
}
