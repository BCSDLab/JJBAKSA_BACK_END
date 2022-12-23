package com.jjbacsa.jjbacsabackend.post.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    private String title;
    private String content;
    private String boardType;
}
