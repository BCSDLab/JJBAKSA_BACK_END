package com.jjbacsa.jjbacsabackend.review.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
    private Long shopId;
    private String content;
    private Integer rate;
    private List<MultipartFile> reviewImages;
}
