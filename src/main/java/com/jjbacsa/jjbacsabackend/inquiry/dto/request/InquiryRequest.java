package com.jjbacsa.jjbacsabackend.inquiry.dto.request;

import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import lombok.*;

import javax.persistence.EntityListeners;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InquiryRequest {
    @NotNull(groups = {ValidationGroups.Create.class}, message = "제목을 입력해주세요")
    private String title;
    @NotNull(groups = {ValidationGroups.Create.class}, message = "문의 내용을 입력해주세요")
    private String content;
    @NotNull(groups = {ValidationGroups.Create.class}, message = "비밀글 여부를 선택해주세요")
    private Boolean isSecret;

}
