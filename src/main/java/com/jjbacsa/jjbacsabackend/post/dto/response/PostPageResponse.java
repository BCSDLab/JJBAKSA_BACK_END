package com.jjbacsa.jjbacsabackend.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
public class PostPageResponse {
    private Long id;
    private String title;
    private String boardType;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date createdAt;

}
