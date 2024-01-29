package com.jjbacsa.jjbacsabackend.etc.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.TokenType;
import com.jjbacsa.jjbacsabackend.user.entity.OAuthInfoEntity;
import com.jjbacsa.jjbacsabackend.user.entity.oauth.OAuth2UserInfo;
import com.jjbacsa.jjbacsabackend.user.entity.oauth.OAuth2UserInfoFactory;
import com.jjbacsa.jjbacsabackend.user.repository.OAuthInfoRepository;
import com.jjbacsa.jjbacsabackend.util.JwtUtil;
import com.jjbacsa.jjbacsabackend.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final OAuthInfoRepository oAuthInfoRepository;
    private final RedisUtil redisUtil;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;
        OAuthType oauthType = OAuthType.valueOf(oAuth2Token.getAuthorizedClientRegistrationId().toUpperCase());
        OAuth2UserInfo oAuth2UserInfo;
        OAuthInfoEntity oAuthInfoEntity;

        oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oauthType, user.getAttributes());
        oAuthInfoEntity = oAuthInfoRepository.findByApiKey(oAuth2UserInfo.getApiKey());

        String accessToken = jwtUtil.generateToken(oAuthInfoEntity.getUser().getId(),
                TokenType.ACCESS, String.valueOf(oAuthInfoEntity.getUser().getUserType().getUserType()));

        String existToken = redisUtil.getStringValue(String.valueOf(oAuthInfoEntity.getUser().getId()));

        if (existToken == null) {
            existToken = jwtUtil.generateToken(oAuthInfoEntity.getUser().getId(), TokenType.REFRESH, oAuthInfoEntity.getUser().getUserType().getUserType());
            redisUtil.setToken(String.valueOf(oAuthInfoEntity.getUser().getId()), existToken);
        }

        response.getWriter().write(objectMapper.writeValueAsString(accessToken));
    }
}

