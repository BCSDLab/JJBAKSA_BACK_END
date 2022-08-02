package com.jjbacsa.jjbacsabackend.user.service;

import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;

import java.util.Map;

public interface UserService {
    UserResponse signUp(UserRequest request) throws Exception;
    Map<String, String> login(UserRequest request) throws Exception;
}
