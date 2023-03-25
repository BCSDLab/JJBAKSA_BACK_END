package com.jjbacsa.jjbacsabackend.user.serviceImpl.oauth;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.user.dto.response.UserResponse;
import com.jjbacsa.jjbacsabackend.user.entity.OAuthInfoEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.OAuthInfoRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class KakaoLogin extends TokenSnsLogin {
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String KAKAO_USERINFO_URI;

    private final OAuthInfoRepository oAuthInfoRepository;

    @Override
    public UserResponse snsLoginByToken(String accessToken) throws Exception {
        return this.requestUserProfileByAccessToken(accessToken, KAKAO_USERINFO_URI);
    }

    @Override
    public OAuthType getOAuthType() {
        return OAuthType.KAKAO;
    }

    @Transactional
    @Override
    UserResponse profileParsing(ResponseEntity<String> response) throws Exception {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
        JSONObject kakaoAccount = (JSONObject) jsonObject.get("kakao_account");
        JSONObject profile = (JSONObject) kakaoAccount.get("profile");
        JSONObject properties = (JSONObject) jsonObject.get("properties");
        String apiKey = String.valueOf(jsonObject.get("id"));

        Optional<OAuthInfoEntity> oauthOptional =
                oAuthInfoRepository.findByApiKeyAndOauthType(apiKey, this.getOAuthType());

        ImageEntity imageEntity = ImageEntity.builder()
                .path(String.valueOf(profile.get("profile_image_url")))
                .url(String.valueOf(profile.get("profile_image_url")))
                .originalName("original_name")
                .build();

        UserEntity kakaoUser = UserEntity.builder()
                .email(kakaoAccount.get("email").toString())
                .nickname(properties.get("nickname").toString())
                .profileImage(imageEntity)
                .userType(UserType.NORMAL)
                .authEmail(true)
                .build();

        if (oauthOptional.isEmpty()) {
            OAuthInfoEntity oAuthInfoEntity = OAuthInfoEntity.builder()
                    .oauthType(this.getOAuthType())
                    .apiKey(apiKey)
                    .user(kakaoUser)
                    .build();

            oAuthInfoRepository.save(oAuthInfoEntity);
        }

        return UserMapper.INSTANCE.toUserResponse(kakaoUser);
    }

}
