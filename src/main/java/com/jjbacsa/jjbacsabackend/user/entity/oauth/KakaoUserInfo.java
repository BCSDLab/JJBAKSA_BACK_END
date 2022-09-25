package com.jjbacsa.jjbacsabackend.user.entity.oauth;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo{

    private Map<String, Object> attributes;
    private Map<String, Object> attributesProperties;
    private Map<String, Object> attributesAccount;
    private Map<String, Object> attributesProfile;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.attributesProperties = (Map<String, Object>) attributes.get("properties");
        this.attributesAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.attributesProfile = (Map<String, Object>) attributesAccount.get("profile");
    }

    @Override
    public String getApiKey() {
        return attributes.get("id").toString();
    }

    @Override
    public OAuthType getOAuthType() {
        return OAuthType.KAKAO;
    }

    @Override
    public String getEmail() {
        return (String) attributesAccount.get("email");
    }

    @Override
    public String getName() {
        return (String) attributesProperties.get("nickname");
    }

    @Override
    public String getProfileImage() {
        return (String) attributesProperties.get("profile_image");
    }
}
