package com.jjbacsa.jjbacsabackend.user.service;

import com.jjbacsa.jjbacsabackend.user.dto.response.UserResponse;

public interface RootUserService {
    UserResponse adminAuthority(String account);
    UserResponse normalAuthority(String account);
}
