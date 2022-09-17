package com.jjbacsa.jjbacsabackend.image.repository;

import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
}
