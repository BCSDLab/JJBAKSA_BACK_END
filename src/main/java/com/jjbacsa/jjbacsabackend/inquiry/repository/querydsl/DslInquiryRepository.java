package com.jjbacsa.jjbacsabackend.inquiry.repository.querydsl;

import com.jjbacsa.jjbacsabackend.inquiry.dto.request.InquiryCursorRequest;
import com.jjbacsa.jjbacsabackend.inquiry.entity.InquiryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DslInquiryRepository {
    Page<InquiryEntity> findAllInquiries(String dateCursor, Long idCursor, Pageable pageable);

    Page<InquiryEntity> findAllMyInquiries(String dateCursor, Long idCursor, Long userId, Pageable pageable);

    Page<InquiryEntity> findAllSearchInquiries(String dateCursor, Long idCursor, String searchWord, Pageable pageable);

    Page<InquiryEntity> findAllSearchMyInquiries(String dateCursor, Long idCursor, String searchWord, Long userId, Pageable pageable);

}
