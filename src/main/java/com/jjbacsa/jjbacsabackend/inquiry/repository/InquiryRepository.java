package com.jjbacsa.jjbacsabackend.inquiry.repository;

import com.jjbacsa.jjbacsabackend.inquiry.entity.InquiryEntity;
import com.jjbacsa.jjbacsabackend.inquiry.repository.querydsl.DslInquiryRepository;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<InquiryEntity, Long>, DslInquiryRepository {

    List<InquiryEntity> findAllByWriter(UserEntity user);
}
