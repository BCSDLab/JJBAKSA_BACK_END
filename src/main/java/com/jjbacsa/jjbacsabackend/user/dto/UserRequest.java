package com.jjbacsa.jjbacsabackend.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String account;
    private String password;
    private String email;
    private String nickname;
}
