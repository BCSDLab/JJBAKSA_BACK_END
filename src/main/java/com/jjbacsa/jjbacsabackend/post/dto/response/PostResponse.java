package com.jjbacsa.jjbacsabackend.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private String title;
    private String content;
    private String boardType;
}