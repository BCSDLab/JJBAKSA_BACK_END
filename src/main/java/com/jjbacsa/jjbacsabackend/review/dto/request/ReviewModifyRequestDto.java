package com.jjbacsa.jjbacsabackend.review.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewModifyRequestDto {
    private Long id;
    private Long shopId;
    private String content;
    private int isTemp;
}
