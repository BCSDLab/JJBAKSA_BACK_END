package com.jjbacsa.jjbacsabackend.user.service;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import org.springframework.data.domain.Page;

public interface UserService {
    UserResponse register(UserRequest request) throws Exception;
    String checkDuplicateAccount(String account) throws Exception;
    Token login(UserRequest request) throws Exception;
    void logout() throws Exception;
    Token refresh() throws Exception;
    UserResponse getLoginUser() throws Exception;
    Page<UserResponse> searchUsers(String keyword, Integer pageSize, Long cursor) throws Exception;
    UserResponse getAccountInfo(Long id) throws Exception;
    UserResponse modifyUser(UserRequest request) throws Exception;
    void withdraw() throws Exception;
    Boolean codeCertification(String email, String code) throws Exception;
}
