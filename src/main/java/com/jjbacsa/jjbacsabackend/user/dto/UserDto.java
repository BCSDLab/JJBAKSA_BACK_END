package com.jjbacsa.jjbacsabackend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String account;
    private String email;
    private String nickname;
}
