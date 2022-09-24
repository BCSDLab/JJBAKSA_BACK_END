package com.jjbacsa.jjbacsabackend.user.entity.oauth;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;

public interface OAuth2UserInfo {
    OAuthType getOAuthType();
    String getApiKey();
    String getEmail();
    String getName();
    String getProfileImage();
}
