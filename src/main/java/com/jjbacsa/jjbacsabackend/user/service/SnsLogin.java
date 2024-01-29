package com.jjbacsa.jjbacsabackend.user.service;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;

public interface SnsLogin {
    UserResponse snsLoginByToken(String accessToken) throws Exception;

    OAuthType getOAuthType() throws Exception;

    void revoke(String authToken) throws Exception;
}
