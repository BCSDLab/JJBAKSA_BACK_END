package com.jjbacsa.jjbacsabackend.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class AuthLinkUtil {
    @Value("${auth-url.web-url}")
    private String webUrl;
    @Value("${auth-url.android-url}")
    private String androidUrl;
    @Value("${auth-url.ios-url}")
    private String iosUrl;

    @Value("${auth-url.google-store-url}")
    private String googleStoreUrl;
    @Value("${auth-url.apple-store-url}")
    private String appleStoreUrl;

    public String getWebUrl(String accessToken, String refreshToken) {
        StringBuilder sb = new StringBuilder();

        sb.append(webUrl);
        return addToken(sb, accessToken, refreshToken).toString();
    }

    public String getAndroidUrl(String accessToken, String refreshToken) {
        StringBuilder sb = new StringBuilder();

        sb.append(androidUrl);
        return addToken(sb, accessToken, refreshToken).toString();
    }

    public String getIosUrl(String accessToken, String refreshToken) {
        StringBuilder sb = new StringBuilder();

        sb.append(iosUrl);
        return addToken(sb, accessToken, refreshToken).toString();
    }


    private StringBuilder addToken(StringBuilder sb, String accessToken, String refreshToken) {
        sb.append("?");
        sb.append("accessToken=").append(accessToken).append("&");
        sb.append("refreshToken=").append(refreshToken);

        return sb;
    }
}
