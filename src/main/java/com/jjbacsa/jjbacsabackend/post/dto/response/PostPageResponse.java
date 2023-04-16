package com.jjbacsa.jjbacsabackend.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostPageResponse {
    private Long id;
    private String title;
    private String boardType;
    private String createdAt;

    public void setCreatedAt(Date createAt){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.createdAt = format.format(createAt);
    }

}
