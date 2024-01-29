package com.jjbacsa.jjbacsabackend.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jjbacsa.jjbacsabackend.image.dto.response.ImageResponse;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Builder
@AllArgsConstructor
public class UserReviewResponse {
    private Long id;
    private String account;
    private String nickname;
    private ImageResponse profileImage;
}
