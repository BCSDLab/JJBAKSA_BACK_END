package com.jjbacsa.jjbacsabackend.inquiry.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerRequest {
    // Todo: Validation
    private String answer;

}
