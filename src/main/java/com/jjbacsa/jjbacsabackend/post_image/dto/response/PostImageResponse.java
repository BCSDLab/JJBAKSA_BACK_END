package com.jjbacsa.jjbacsabackend.post_image.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostImageResponse {
    private String originalName;
    private String imageUrl;
}
