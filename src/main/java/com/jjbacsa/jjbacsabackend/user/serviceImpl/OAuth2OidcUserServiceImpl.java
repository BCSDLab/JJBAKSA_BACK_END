package com.jjbacsa.jjbacsabackend.user.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.user.entity.CustomUserDetails;
import com.jjbacsa.jjbacsabackend.user.entity.OAuthInfoEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.entity.oauth.OAuth2OidcUserInfoFactory;
import com.jjbacsa.jjbacsabackend.user.entity.oauth.OAuth2UserInfo;
import com.jjbacsa.jjbacsabackend.user.repository.OAuthInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2OidcUserServiceImpl extends OidcUserService{

    private final OAuthInfoRepository oAuthInfoRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        return processOAuth2OidcUser(userRequest, oidcUser);
    }

    private OidcUser processOAuth2OidcUser(OidcUserRequest oidcUserRequest, OidcUser oidcUser) {

        OAuthType oauthType = OAuthType.valueOf(oidcUserRequest.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2UserInfo oAuth2UserInfo = OAuth2OidcUserInfoFactory.getOAuth2OidcUserInfo(oauthType, oidcUser.getAttributes());

        Optional<OAuthInfoEntity> oauthOptional =
                oAuthInfoRepository.findByApiKeyAndOauthType(oAuth2UserInfo.getApiKey(), oAuth2UserInfo.getOAuthType());

        ImageEntity imageEntity = ImageEntity.builder()
                .path(oAuth2UserInfo.getProfileImage())
                .originalName("original_name")
                .build();

        UserEntity user = UserEntity.builder()
                .email(oAuth2UserInfo.getEmail())
                .nickname(oAuth2UserInfo.getName())
                .userType(UserType.NORMAL)
                .profileImage(imageEntity)
                .build();

        // 회원 가입
        if(oauthOptional.isEmpty()) {
            registerOidc(oAuth2UserInfo, user);
        }

        return new CustomUserDetails(user, oidcUser.getAttributes());
    }

    private void registerOidc(OAuth2UserInfo oAuth2UserInfo, UserEntity user) {

        OAuthInfoEntity oAuthInfoEntity = OAuthInfoEntity.builder()
                .oauthType(oAuth2UserInfo.getOAuthType())
                .apiKey(oAuth2UserInfo.getApiKey())
                .user(user)
                .build();

        oAuthInfoRepository.save(oAuthInfoEntity);
    }

}
