package com.jjbacsa.jjbacsabackend.user.entity.oauth;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(OAuthType oauthType, Map<String, Object> attributes) {
        switch (oauthType) {
            case GOOGLE: return new GoogleUserInfo(attributes);
            case NAVER: return new NaverUserInfo(attributes);
            case KAKAO: return new KakaoUserInfo(attributes);
            // TODO: APPLE
            default: throw new IllegalArgumentException("Invalid OAuthType.");
        }
    }
}