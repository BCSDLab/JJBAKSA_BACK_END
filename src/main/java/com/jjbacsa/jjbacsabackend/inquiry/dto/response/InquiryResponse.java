package com.jjbacsa.jjbacsabackend.inquiry.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jjbacsa.jjbacsabackend.inquiry_image.dto.response.InquiryImageResponse;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class InquiryResponse {
    private Long id;
    private String title;
    private String content;
    private String answer;
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date createdAt;
    private List<InquiryImageResponse> inquiryImages;
    private int isSecreted;

}
