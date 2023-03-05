package com.jjbacsa.jjbacsabackend.user.serviceImpl.oauth;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.image.entity.ImageEntity;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
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
public class NaverLogin extends TokenSnsLogin {
    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String NAVER_USERINFO_URI;

    private final OAuthInfoRepository oAuthInfoRepository;

    @Override
    public UserResponse snsLoginByToken(String accessToken) throws Exception {
        return this.requestUserProfileByAccessToken(accessToken, NAVER_USERINFO_URI);
    }

    @Override
    public OAuthType getOAuthType() {
        return OAuthType.NAVER;
    }

    @Transactional
    @Override
    UserResponse profileParsing(ResponseEntity<String> response) throws Exception {

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
        JSONObject obj = (JSONObject) jsonObject.get("response");

        String apiKey = String.valueOf(obj.get("id"));
        String profile = (String) obj.get("profile_image");

        Optional<OAuthInfoEntity> oauthOptional =
                oAuthInfoRepository.findByApiKeyAndOauthType(apiKey, this.getOAuthType());

        ImageEntity imageEntity = ImageEntity.builder()
                .path(profile)
                .url(profile)
                .originalName("original_name")
                .build();

        UserEntity naverUser = UserEntity.builder()
                .email(obj.get("email").toString())
                .nickname(obj.get("nickname").toString())
                .profileImage(imageEntity)
                .userType(UserType.NORMAL)
                .authEmail(true)
                .build();

        if(oauthOptional.isEmpty()) {
            OAuthInfoEntity oAuthInfoEntity = OAuthInfoEntity.builder()
                    .oauthType(this.getOAuthType())
                    .apiKey(apiKey)
                    .user(naverUser)
                    .build();

            oAuthInfoRepository.save(oAuthInfoEntity);
        }

        return UserMapper.INSTANCE.toUserResponse(naverUser);
    }
}
