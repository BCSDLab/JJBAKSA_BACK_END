package com.jjbacsa.jjbacsabackend.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCountResponse {
    private Long id;
    private Integer reviewCount = 0;
    private Integer friendCount = 0;
}
