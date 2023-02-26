package com.jjbacsa.jjbacsabackend.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private String title;
    private String content;
    private String boardType;
    private String createdAt;

    public void setCreatedAt(Date createAt){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        this.createdAt = format.format(createAt);
    }
}