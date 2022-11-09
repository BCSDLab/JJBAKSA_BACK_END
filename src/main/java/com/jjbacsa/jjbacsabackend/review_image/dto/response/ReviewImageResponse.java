package com.jjbacsa.jjbacsabackend.review_image.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewImageResponse {
    Long imageId;
    String originalName;
    String path;
    String imageUrl;
}
