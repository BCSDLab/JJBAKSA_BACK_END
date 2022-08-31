package com.jjbacsa.jjbacsabackend.user.service;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;

import javax.servlet.http.HttpServletResponse;

public interface UserService {
    UserResponse register(UserRequest request) throws Exception;
    Token login(UserRequest request, HttpServletResponse response) throws Exception;
    void logout(HttpServletResponse response) throws Exception;
    UserResponse getLoginUser() throws Exception;
}
