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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
@Transactional
public class KakaoLogin extends TokenSnsLogin {
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String KAKAO_USERINFO_URI;

    @Value("${spring.security.oauth2.client.registration.kakao.admin-key}")
    private String appAdminKey;

    @Value("${spring.security.oauth2.client.provider.kakao.unlink-uri}")
    private String KAKAO_UNLINK_URI;

    private final OAuthInfoRepository oAuthInfoRepository;

    @Override
    public UserResponse snsLoginByToken(String accessToken) throws Exception {
        return this.requestUserProfileByAccessToken(accessToken, KAKAO_USERINFO_URI);
    }

    @Override
    public OAuthType getOAuthType() {
        return OAuthType.KAKAO;
    }

    @Override
    public void revoke(String accessToken) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);

        restTemplate.postForEntity(KAKAO_UNLINK_URI, httpEntity, String.class);
    }

    @Transactional
    @Override
    UserResponse profileParsing(ResponseEntity<String> response) throws Exception {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
        JSONObject kakaoAccount = (JSONObject) jsonObject.get("kakao_account");
        //JSONObject profile = (JSONObject) kakaoAccount.get("profile");
        JSONObject properties = (JSONObject) jsonObject.get("properties");
        String apiKey = String.valueOf(jsonObject.get("id"));

        Optional<OAuthInfoEntity> oauthOptional =
                oAuthInfoRepository.findByApiKeyAndOauthType(apiKey, this.getOAuthType());

        UserEntity kakaoUser = UserEntity.builder()
                .email(kakaoAccount.get("email").toString())
                .nickname(properties.get("nickname").toString())
                //.profileImage(imageEntity)
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
