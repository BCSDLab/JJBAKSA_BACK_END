package com.jjbacsa.jjbacsabackend.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewCountResponse {
    Long count;
}
