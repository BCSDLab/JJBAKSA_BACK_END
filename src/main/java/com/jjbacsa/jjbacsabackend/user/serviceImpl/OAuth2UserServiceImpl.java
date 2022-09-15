package com.jjbacsa.jjbacsabackend.user.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.user.entity.CustomUserDetails;
import com.jjbacsa.jjbacsabackend.user.entity.OAuthInfoEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.entity.oauth.OAuth2UserInfo;
import com.jjbacsa.jjbacsabackend.user.entity.oauth.OAuth2UserInfoFactory;
import com.jjbacsa.jjbacsabackend.user.repository.OAuthInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final OAuthInfoRepository oAuthInfoRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {

        OAuthType oauthType = OAuthType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oauthType, oAuth2User.getAttributes());

        Optional<OAuthInfoEntity> oauthOptional =
                oAuthInfoRepository.findByApiKeyAndOauthType(oAuth2UserInfo.getApiKey(), oAuth2UserInfo.getOAuthType());

        // TODO : dto
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
            registerOAuth(oAuth2UserInfo, user);
        }

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }

    private OAuthInfoEntity registerOAuth(OAuth2UserInfo oAuth2UserInfo, UserEntity user) {

        OAuthInfoEntity oAuthInfoEntity = OAuthInfoEntity.builder()
                .oauthType(oAuth2UserInfo.getOAuthType())
                .apiKey(oAuth2UserInfo.getApiKey())
                .user(user)
                .build();

        return oAuthInfoRepository.save(oAuthInfoEntity);
    }
}
