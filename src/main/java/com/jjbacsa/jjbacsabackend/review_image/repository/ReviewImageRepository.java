package com.jjbacsa.jjbacsabackend.review_image.repository;

import com.jjbacsa.jjbacsabackend.review_image.entity.ReviewImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImageRepository extends JpaRepository<ReviewImageEntity, Long> {
}
