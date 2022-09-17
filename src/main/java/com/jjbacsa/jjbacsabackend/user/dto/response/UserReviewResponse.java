package com.jjbacsa.jjbacsabackend.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReviewResponse {
    private Long id;
    private String account;
    private String nickname;
}
