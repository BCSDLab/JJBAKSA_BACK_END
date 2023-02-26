package com.jjbacsa.jjbacsabackend.inquiry.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InquiryRequest {
    // Todo: Validation
    private String title;
    private String content;
    private String secret;

}
