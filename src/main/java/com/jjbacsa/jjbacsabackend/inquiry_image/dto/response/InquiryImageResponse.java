package com.jjbacsa.jjbacsabackend.inquiry_image.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InquiryImageResponse {
    private String originalName;
    private String path;
    private String imageUrl;
}
