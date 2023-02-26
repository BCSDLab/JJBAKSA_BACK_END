package com.jjbacsa.jjbacsabackend.inquiry.repository;

import com.jjbacsa.jjbacsabackend.inquiry.entity.InquiryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<InquiryEntity, Long> {
}
