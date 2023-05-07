package com.jjbacsa.jjbacsabackend.inquiry.dto.request;

import com.jjbacsa.jjbacsabackend.etc.annotations.IsValidListSize;
import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class InquiryRequest {
    @Pattern(regexp = "^[a-zA-z가-힣0-9]{1,20}$", message = "작성자에 특수문자와 초성은 불가능합니다.")
    private String createdBy;
    @NotNull(message = "제목을 입력해주세요")
    private String title;
    @NotNull(message = "문의 내용을 입력해주세요")
    private String content;
    @NotNull(message = "비밀글 여부를 선택해주세요")
    private Boolean isSecret;
    @IsValidListSize(max = 3, message = "문의글 이미지는 최대 3개 입니다.")
    private List<MultipartFile> inquiryImages;
}
