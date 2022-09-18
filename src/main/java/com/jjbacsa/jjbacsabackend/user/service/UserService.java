package com.jjbacsa.jjbacsabackend.user.service;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse register(UserRequest request) throws Exception;
    String checkDuplicateAccount(String account) throws Exception;
    Token login(UserRequest request) throws Exception;
    void logout() throws Exception;
    Token refresh() throws Exception;
    UserResponse getLoginUser() throws Exception;
    Page<UserResponse> searchUsers(String keyword, String cursor, Pageable pageable) throws Exception;
}
