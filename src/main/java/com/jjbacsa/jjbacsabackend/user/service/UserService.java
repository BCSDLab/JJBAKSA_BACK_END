package com.jjbacsa.jjbacsabackend.user.service;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.user.dto.*;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

public interface UserService {
    UserResponse register(UserRequest request) throws Exception;

    URI authEmail(String accessToken, String refreshToken) throws Exception;

    String checkDuplicateAccount(String account) throws Exception;

    Token login(UserRequest request) throws Exception;

    void logout() throws Exception;

    Token refresh() throws Exception;

    UserResponse getLoginUser() throws Exception;

    Page<UserResponseWithFollowedType> searchUsers(String keyword, Integer pageSize, Long cursor) throws Exception;

    UserResponse getAccountInfo(Long id) throws Exception;

    UserResponse modifyUser(UserModifyRequest request) throws Exception;

    void withdraw() throws Exception;

    WithdrawReasonResponse createWithdrawReason(WithdrawRequest request) throws Exception;

    UserResponse modifyProfile(MultipartFile profile) throws Exception;

    void sendAuthEmailCode(String email) throws Exception;

    void sendAuthEmailCode(String account, String email) throws Exception;

    void sendAuthEmailLink(String email) throws Exception;

    UserResponse findAccount(String email, String code) throws Exception;

    String findPassword(EmailRequest request) throws Exception;

    UserResponse validatePassword(String password) throws Exception;
}
