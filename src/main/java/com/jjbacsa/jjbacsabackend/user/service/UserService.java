package com.jjbacsa.jjbacsabackend.user.service;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.user.dto.EmailRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserRequest;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

public interface UserService {
    UserResponse register(UserRequest request) throws Exception;

    ModelAndView authEmail(String accessToken, String refreshToken) throws Exception;

    UserResponse modifyNickname(String nickname) throws Exception;

    String checkDuplicateAccount(String account) throws Exception;

    Token login(UserRequest request) throws Exception;

    void logout() throws Exception;

    Token refresh() throws Exception;

    UserResponse getLoginUser() throws Exception;

    Page<UserResponse> searchUsers(String keyword, Integer pageSize, Long cursor) throws Exception;

    UserResponse getAccountInfo(Long id) throws Exception;

    UserResponse modifyUser(UserRequest request) throws Exception;

    void withdraw() throws Exception;

    UserResponse modifyProfile(MultipartFile profile) throws Exception;

    void sendAuthEmailCode(String email) throws Exception;

    void sendAuthEmailLink(String email) throws Exception;

    UserResponse findAccount(String email, String code) throws Exception;

    String findPassword(EmailRequest request) throws Exception;

    UserResponse modifyPassword(String password) throws Exception;
}
