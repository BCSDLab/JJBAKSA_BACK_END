package com.jjbacsa.jjbacsabackend.user.entity.oauth;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo{

    private Map<String, Object> attributes;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = (Map<String, Object>)attributes.get("response");
    }

    @Override
    public String getApiKey() {
        return (String) attributes.get("id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getProfileImage() {
        return (String) attributes.get("profile_image");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public OAuthType getOAuthType() {
        return OAuthType.NAVER;
    }
}
