package com.jjbacsa.jjbacsabackend.review.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
public class ReviewDateResponse {
    @JsonFormat(pattern = "yy-MM-dd", timezone = "Asia/Seoul")
    private Date lastDate;
}
