package com.jjbacsa.jjbacsabackend.image.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageRequest {
    private String path;
    private String originalName;
}
