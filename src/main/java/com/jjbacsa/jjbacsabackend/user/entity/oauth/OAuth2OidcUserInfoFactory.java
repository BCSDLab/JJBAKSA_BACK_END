package com.jjbacsa.jjbacsabackend.user.entity.oauth;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;

import java.util.Map;

public class OAuth2OidcUserInfoFactory {
    public static OAuth2UserInfo getOAuth2OidcUserInfo(OAuthType oauthType, Map<String, Object> attributes) {
        switch (oauthType) {
            case APPLE: return new AppleUserInfo(attributes);
            case GOOGLE: return new GoogleUserInfo(attributes);
            default: throw new IllegalArgumentException("Invalid OAuthType");
        }
    }
}
