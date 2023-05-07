package com.jjbacsa.jjbacsabackend.inquiry.dto.response;

import com.jjbacsa.jjbacsabackend.inquiry_image.dto.response.InquiryImageResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryResponse {
    private Long id;
    private String title;
    private String content;
    private String answer;
    private String createdBy;
    private String createdAt;
    private List<InquiryImageResponse> inquiryImages;
    private int isSecreted;

    public void setCreatedAt(Date createAt){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.createdAt = format.format(createAt);
    }

    public void setSecret() {
        this.content = "비밀글 입니다.";
        this.answer = "비밀글 입니다.";
    }
}
