package com.jjbacsa.jjbacsabackend.user.service;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;

public interface UserService {
    UserResponse register(UserRequest request) throws Exception;
    Token login(UserRequest request) throws Exception;
    UserResponse getLoginUser() throws Exception;
}
