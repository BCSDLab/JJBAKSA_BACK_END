package com.jjbacsa.jjbacsabackend.inquiry.service;

import com.jjbacsa.jjbacsabackend.inquiry.dto.request.AnswerRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.request.InquiryCursorRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.request.InquiryRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.response.InquiryResponse;
import org.springframework.data.domain.Page;

public interface InquiryService {
    InquiryResponse getInquiry(Long inquiryId) throws Exception;

    InquiryResponse create(InquiryRequest inquiryRequest) throws Exception;

    InquiryResponse modify(InquiryRequest inquiryRequest, Long inquiryId) throws Exception;

    void delete(Long inquiryId) throws Exception;

    InquiryResponse addAnswer(AnswerRequest answer, Long inquiryId);

    Page<InquiryResponse> getInquiries(InquiryCursorRequest inquiryCursorRequest);

    Page<InquiryResponse> getMyInquiries(InquiryCursorRequest inquiryCursorRequest) throws Exception;

    Page<InquiryResponse> searchInquiries(InquiryCursorRequest inquiryCursorRequest, String searchWord);

    Page<InquiryResponse> searchMyInquiries(InquiryCursorRequest inquiryCursorRequest, String searchWord) throws Exception;


}
