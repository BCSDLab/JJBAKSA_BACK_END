package com.jjbacsa.jjbacsabackend.etc.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.TokenType;
import com.jjbacsa.jjbacsabackend.user.entity.OAuthInfoEntity;
import com.jjbacsa.jjbacsabackend.user.entity.oauth.OAuth2OidcUserInfoFactory;
import com.jjbacsa.jjbacsabackend.user.entity.oauth.OAuth2UserInfo;
import com.jjbacsa.jjbacsabackend.user.entity.oauth.OAuth2UserInfoFactory;
import com.jjbacsa.jjbacsabackend.user.repository.OAuthInfoRepository;
import com.jjbacsa.jjbacsabackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final OAuthInfoRepository oAuthInfoRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OidcUser user = (OidcUser) authentication.getPrincipal();
        OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;
        OAuthType oauthType = OAuthType.valueOf(oAuth2Token.getAuthorizedClientRegistrationId().toUpperCase());
        OAuth2UserInfo oAuth2UserInfo;
        OAuthInfoEntity oAuthInfoEntity;

        if(oauthType.equals(OAuthType.KAKAO) || oauthType.equals(OAuthType.NAVER)) {
            oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oauthType, user.getAttributes());
        } else {
            oAuth2UserInfo = OAuth2OidcUserInfoFactory.getOAuth2OidcUserInfo(oauthType, user.getAttributes());
        }
        oAuthInfoEntity = oAuthInfoRepository.findByApiKey(oAuth2UserInfo.getApiKey());

        String accessToken = jwtUtil.generateToken(oAuthInfoEntity.getUser().getId(), 
                TokenType.ACCESS, oAuthInfoEntity.getUser().getUserType().getUserType());
        String RefreshToken = jwtUtil.generateToken(oAuthInfoEntity.getUser().getId(),
                TokenType.REFRESH, oAuthInfoEntity.getUser().getUserType().getUserType());

        response.getWriter().write("accessToken : " + objectMapper.writeValueAsString(accessToken));
        response.getWriter().write("\nrefreshToken : " + objectMapper.writeValueAsString(RefreshToken));
    }
}

