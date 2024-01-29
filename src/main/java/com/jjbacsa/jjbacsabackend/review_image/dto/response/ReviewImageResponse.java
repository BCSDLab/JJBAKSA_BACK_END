package com.jjbacsa.jjbacsabackend.review_image.dto.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewImageResponse {
    String originalName;
    String imageUrl;
}
