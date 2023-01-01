package com.jjbacsa.jjbacsabackend.user.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.TokenType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.user.entity.CustomUserDetails;
import com.jjbacsa.jjbacsabackend.user.entity.OAuthInfoEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.entity.oauth.OAuth2UserInfo;
import com.jjbacsa.jjbacsabackend.user.entity.oauth.OAuth2UserInfoFactory;
import com.jjbacsa.jjbacsabackend.user.repository.OAuthInfoRepository;
import com.jjbacsa.jjbacsabackend.util.JwtUtil;
import com.jjbacsa.jjbacsabackend.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final OAuthInfoRepository oAuthInfoRepository;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        return processOAuth2User(userRequest, oAuth2User);
    }

    public Token appleLogin() throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) Objects
                .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String idToken = request.getHeader("Authorization");

        Map<String, Object> payloads = jwtUtil.getPayloadsFromJwt(idToken);

        Optional<OAuthInfoEntity> oauthOptional =
                oAuthInfoRepository.findByApiKeyAndOauthType(String.valueOf(payloads.get("sub")), OAuthType.APPLE);

        UserEntity user = UserEntity.builder()
                .email(String.valueOf(payloads.get("email")))
                .nickname("APPLE_" + String.valueOf(payloads.get("sub")).substring(15, 20))
                .userType(UserType.NORMAL)
                .authEmail(true)
                .build();

        if(oauthOptional.isEmpty()) {
            OAuthInfoEntity oAuthInfoEntity = OAuthInfoEntity.builder()
                    .oauthType(OAuthType.APPLE)
                    .apiKey(String.valueOf(payloads.get("sub")))
                    .user(user)
                    .build();

            oAuthInfoRepository.save(oAuthInfoEntity);
        }

        String existToken = redisUtil.getStringValue(String.valueOf(user.getId()));

        if (existToken == null) {
            existToken = jwtUtil.generateToken(user.getId(), TokenType.REFRESH, user.getUserType().getUserType());
            redisUtil.setToken(String.valueOf(user.getId()), existToken);
        }

        return new Token(jwtUtil.generateToken(user.getId(), TokenType.ACCESS, user.getUserType().getUserType()), existToken);
    }


    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {

        OAuthType oauthType = OAuthType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oauthType, oAuth2User.getAttributes());

        Optional<OAuthInfoEntity> oauthOptional =
                oAuthInfoRepository.findByApiKeyAndOauthType(oAuth2UserInfo.getApiKey(), oAuth2UserInfo.getOAuthType());

        // TODO : dto
        ImageEntity imageEntity = ImageEntity.builder()
                .path(oAuth2UserInfo.getProfileImage())
                .url(oAuth2UserInfo.getProfileImage())
                .originalName("original_name")
                .build();

        UserEntity user = UserEntity.builder()
                .email(oAuth2UserInfo.getEmail())
                .nickname(oAuth2UserInfo.getName())
                .userType(UserType.NORMAL)
                .profileImage(imageEntity)
                .authEmail(true)
                .build();

        // 회원 가입
        if(oauthOptional.isEmpty()) {
            registerOAuth(oAuth2UserInfo, user);
        }

        return new CustomUserDetails(user.getId(), oAuth2User.getAttributes());
    }

    private void registerOAuth(OAuth2UserInfo oAuth2UserInfo, UserEntity user) {

        OAuthInfoEntity oAuthInfoEntity = OAuthInfoEntity.builder()
                .oauthType(oAuth2UserInfo.getOAuthType())
                .apiKey(oAuth2UserInfo.getApiKey())
                .user(user)
                .build();

        oAuthInfoRepository.save(oAuthInfoEntity);
    }
}
