package com.jjbacsa.jjbacsabackend.user.entity.oauth;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(OAuthType oauthType, Map<String, Object> attributes) {
        switch (oauthType) {
            case NAVER: return new NaverUserInfo(attributes);
            case KAKAO: return new KakaoUserInfo(attributes);
            case GOOGLE: return new GoogleUserInfo(attributes);
            default: throw new RequestInputException(ErrorMessage.INVALID_SOCIAL_TYPE);
        }
    }
}