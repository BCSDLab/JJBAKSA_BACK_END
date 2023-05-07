package com.jjbacsa.jjbacsabackend.inquiry_image.repository;

import com.jjbacsa.jjbacsabackend.inquiry_image.entity.InquiryImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InquiryImageRepository extends JpaRepository<InquiryImageEntity, Long> {
}
