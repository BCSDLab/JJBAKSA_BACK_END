package com.jjbacsa.jjbacsabackend.user.serviceImpl.oauth;

import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class NaverLogin extends TokenSnsLogin {
    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String NAVER_USERINFO_URI;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.provider.naver.revoke-uri}")
    private String NAVER_REVOKE_URI;

    private final OAuthInfoRepository oAuthInfoRepository;

    @Override
    public UserResponse snsLoginByToken(String accessToken) throws Exception {
        return this.requestUserProfileByAccessToken(accessToken, NAVER_USERINFO_URI);
    }

    @Override
    public OAuthType getOAuthType() {
        return OAuthType.NAVER;
    }

    @Override
    public void revoke(String accessToken) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("client_id", CLIENT_ID);
        parameters.add("client_secret", CLIENT_SECRET);
        parameters.add("access_token", accessToken);
        parameters.add("grant_type", "delete");
        parameters.add("service_provider", "NAVER");

        restTemplate.postForEntity(NAVER_REVOKE_URI, parameters, String.class);
    }

    @Transactional
    @Override
    UserResponse profileParsing(ResponseEntity<String> response) throws Exception {

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
        JSONObject obj = (JSONObject) jsonObject.get("response");

        String apiKey = String.valueOf(obj.get("id"));

        Optional<OAuthInfoEntity> oauthOptional =
                oAuthInfoRepository.findByApiKeyAndOauthType(apiKey, this.getOAuthType());

        UserEntity naverUser = UserEntity.builder()
                .account("sns-" + UUID.randomUUID())
                .email(obj.get("email").toString())
                .nickname(obj.get("nickname").toString())
                .userType(UserType.NORMAL)
                .authEmail(true)
                .build();

        if (oauthOptional.isEmpty()) {
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
