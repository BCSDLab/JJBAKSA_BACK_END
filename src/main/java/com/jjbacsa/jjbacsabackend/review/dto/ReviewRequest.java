package com.jjbacsa.jjbacsabackend.review.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    private String content;
    private boolean isTemp;
}
