package com.jjbacsa.jjbacsabackend.inquiry.dto.request;

import com.jjbacsa.jjbacsabackend.etc.annotations.ValidationGroups;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerRequest {
    @NotNull(groups = {ValidationGroups.Create.class}, message = "답변을 입력해주세요")
    private String answer;

}
