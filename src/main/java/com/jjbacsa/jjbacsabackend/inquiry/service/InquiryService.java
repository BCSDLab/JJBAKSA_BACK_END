package com.jjbacsa.jjbacsabackend.inquiry.service;

import com.jjbacsa.jjbacsabackend.inquiry.dto.request.AnswerRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.request.InquiryRequest;
import com.jjbacsa.jjbacsabackend.inquiry.dto.response.InquiryResponse;

public interface InquiryService {
    InquiryResponse getInquiry(Long inquiryId) throws Exception;

    InquiryResponse create(InquiryRequest inquiryRequest) throws Exception;

    InquiryResponse modify(InquiryRequest inquiryRequest, Long inquiryId) throws Exception;

    void delete(Long inquiryId) throws Exception;

    InquiryResponse addAnswer(AnswerRequest answer, Long inquiryId);
}
