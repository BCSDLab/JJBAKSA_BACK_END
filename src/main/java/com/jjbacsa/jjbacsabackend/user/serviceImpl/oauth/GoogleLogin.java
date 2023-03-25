package com.jjbacsa.jjbacsabackend.user.serviceImpl.oauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.user.dto.response.UserResponse;
import com.jjbacsa.jjbacsabackend.user.entity.OAuthInfoEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.OAuthInfoRepository;
import com.jjbacsa.jjbacsabackend.user.service.SnsLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GoogleLogin implements SnsLogin {

    private final OAuthInfoRepository oAuthInfoRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String GOOGLE_CLIENT_ID;

    @Transactional
    @Override
    public UserResponse snsLoginByToken(String idToken) throws Exception {
        GoogleIdToken claims = verifyToken(idToken);

        Optional<OAuthInfoEntity> oauthOptional =
                oAuthInfoRepository.findByApiKeyAndOauthType(String.valueOf(claims.getPayload().get("sub")), this.getOAuthType());

        UserEntity googleUser = UserEntity.builder()
                .email(String.valueOf(claims.getPayload().get("email")))
                .nickname(String.valueOf(claims.getPayload().get("name")))
                .userType(UserType.NORMAL)
                .authEmail(true)
                .build();

        if (oauthOptional.isEmpty()) {
            OAuthInfoEntity oAuthInfoEntity = OAuthInfoEntity.builder()
                    .oauthType(this.getOAuthType())
                    .apiKey(String.valueOf(claims.getPayload().get("sub")))
                    .user(googleUser)
                    .build();

            oAuthInfoRepository.save(oAuthInfoEntity);
        }

        return UserMapper.INSTANCE.toUserResponse(googleUser);
    }

    @Override
    public OAuthType getOAuthType() throws Exception {
        return OAuthType.GOOGLE;
    }

    private GoogleIdToken verifyToken(String idToken) throws Exception {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                .build();

        GoogleIdToken googleIdToken = verifier.verify(idToken);

        if (googleIdToken == null) {
            throw new RequestInputException(ErrorMessage.INVALID_TOKEN);
        }

        return googleIdToken;
    }
}
