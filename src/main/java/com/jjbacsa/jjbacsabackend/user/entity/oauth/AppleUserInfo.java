package com.jjbacsa.jjbacsabackend.user.entity.oauth;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;

import java.util.Map;

public class AppleUserInfo implements OAuth2UserInfo{

    private Map<String, Object> attributes;

    public AppleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getApiKey() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getProfileImage() {
        return (String) attributes.get("picture");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public OAuthType getOAuthType() {
        return OAuthType.APPLE;
    }
}
