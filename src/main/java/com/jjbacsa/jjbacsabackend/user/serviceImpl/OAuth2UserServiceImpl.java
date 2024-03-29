package com.jjbacsa.jjbacsabackend.user.serviceImpl;

import com.jjbacsa.jjbacsabackend.etc.dto.Token;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.TokenType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.follow.service.InternalFollowService;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.entity.CustomUserDetails;
import com.jjbacsa.jjbacsabackend.user.entity.OAuthInfoEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.entity.oauth.OAuth2UserInfo;
import com.jjbacsa.jjbacsabackend.user.entity.oauth.OAuth2UserInfoFactory;
import com.jjbacsa.jjbacsabackend.user.repository.OAuthInfoRepository;
import com.jjbacsa.jjbacsabackend.user.repository.UserCountRepository;
import com.jjbacsa.jjbacsabackend.user.service.InternalUserService;
import com.jjbacsa.jjbacsabackend.user.service.SnsLogin;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final OAuthInfoRepository oAuthInfoRepository;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final List<SnsLogin> snsLoginList;
    private final InternalUserService userService;
    private final UserCountRepository userCountRepository;
    private final InternalFollowService followService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        return processOAuth2User(userRequest, oAuth2User);
    }

    @Transactional
    public Token oAuthLoginByToken(OAuthType oAuthType) throws Exception {
        SnsLogin snsLogin = this.initSnsService(oAuthType);

        HttpServletRequest request = ((ServletRequestAttributes) Objects
                .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = request.getHeader("Authorization");

        UserResponse user = snsLogin.snsLoginByToken(token);
        Long userId = oAuthInfoRepository.getUserId(user.getEmail(), oAuthType);

        String existToken = redisUtil.getStringValue(String.valueOf(userId));

        if (existToken == null) {
            existToken = jwtUtil.generateToken(userId, TokenType.REFRESH, user.getUserType().getUserType());
            redisUtil.setToken(String.valueOf(userId), existToken);
        }

        return new Token(jwtUtil.generateToken(userId, TokenType.ACCESS, user.getUserType().getUserType()), existToken);
    }

    @Transactional
    public void revokeSnsUser(String authToken, OAuthType oAuthType) throws Exception {
        SnsLogin snsLogin = this.initSnsService(oAuthType);

        UserEntity user = userService.getLoginUser();

        OAuthInfoEntity oAuthInfoEntity = getOAuthUserByUserId(user.getId());

        userCountRepository.updateAllFriendsCountByUser(user);
        followService.deleteFollowWithUser(user);

        user.setIsDeleted(1);
        oAuthInfoEntity.setIsDeleted(1);

        String existToken = redisUtil.getStringValue(String.valueOf(user.getId()));
        if (existToken != null) redisUtil.deleteValue(String.valueOf(user.getId()));

        snsLogin.revoke(authToken);
    }


    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {

        OAuthType oauthType = OAuthType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oauthType, oAuth2User.getAttributes());

        Optional<OAuthInfoEntity> oauthOptional =
                oAuthInfoRepository.findByApiKeyAndOauthType(oAuth2UserInfo.getApiKey(), oAuth2UserInfo.getOAuthType());

        UserEntity user = UserEntity.builder()
                .account("sns-" + UUID.randomUUID())
                .email(oAuth2UserInfo.getEmail())
                .nickname(oAuth2UserInfo.getName())
                .userType(UserType.NORMAL)
                .authEmail(true)
                .build();

        // 회원 가입
        if (oauthOptional.isEmpty()) {
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

    private SnsLogin initSnsService(OAuthType oAuthType) throws Exception {

        for (SnsLogin snsLogin : snsLoginList) {
            if (snsLogin.getOAuthType().equals(oAuthType)) {
                return snsLogin;
            }
        }

        throw new RequestInputException(ErrorMessage.INVALID_TOKEN_TYPE);
    }

    private OAuthInfoEntity getOAuthUserByUserId(Long userId) {
        return oAuthInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.USER_NOT_EXISTS_EXCEPTION));
    }

}
