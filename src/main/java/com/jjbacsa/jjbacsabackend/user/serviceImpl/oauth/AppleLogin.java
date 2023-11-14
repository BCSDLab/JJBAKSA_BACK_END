package com.jjbacsa.jjbacsabackend.user.serviceImpl.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.enums.OAuthType;
import com.jjbacsa.jjbacsabackend.etc.enums.UserType;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
import com.jjbacsa.jjbacsabackend.user.dto.UserResponse;
import com.jjbacsa.jjbacsabackend.user.dto.sns.Key;
import com.jjbacsa.jjbacsabackend.user.dto.sns.PublicKeys;
import com.jjbacsa.jjbacsabackend.user.entity.OAuthInfoEntity;
import com.jjbacsa.jjbacsabackend.user.entity.UserEntity;
import com.jjbacsa.jjbacsabackend.user.mapper.UserMapper;
import com.jjbacsa.jjbacsabackend.user.repository.OAuthInfoRepository;
import com.jjbacsa.jjbacsabackend.user.service.SnsLogin;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AppleLogin implements SnsLogin {

    @Value("${spring.security.oauth2.client.provider.apple.client-id}")
    private String APPLE_CLIENT_ID;

    @Value("${spring.security.oauth2.client.provider.apple.key-uri}")
    private String APPLE_KEY_URI;

    @Value("${spring.security.oauth2.client.provider.apple.key-id}")
    private String APPLE_KEY_ID;

    @Value("${spring.security.oauth2.client.provider.apple.team-id}")
    private String APPLE_TEAM_ID;

    @Value("${spring.security.oauth2.client.provider.apple.private-key}")
    private String APPLE_PRIVATE_KEY;

    @Value("${spring.security.oauth2.client.provider.apple.token-uri}")
    private String APPLE_TOKEN_URI;

    @Value("${spring.security.oauth2.client.provider.apple.revoke-uri}")
    private String APPLE_REVOKE_URI;

    private final OAuthInfoRepository oAuthInfoRepository;

    @Transactional
    @Override
    public UserResponse snsLoginByToken(String idToken) throws Exception {
        Claims claims = this.verifyToken(idToken)
                .orElseThrow(() -> new RequestInputException(ErrorMessage.INVALID_TOKEN));

        Optional<OAuthInfoEntity> oauthOptional =
                oAuthInfoRepository.findByApiKeyAndOauthType(String.valueOf(claims.get("sub")), this.getOAuthType());

        UserEntity appleUser = UserEntity.builder()
                .account("sns-" + UUID.randomUUID())
                .email(String.valueOf(claims.get("email")))
                .nickname(this.getOAuthType() + "_" + String.valueOf(claims.get("sub")).substring(15, 20))
                .userType(UserType.NORMAL)
                .authEmail(true)
                .build();

        if (oauthOptional.isEmpty()) {
            OAuthInfoEntity oAuthInfoEntity = OAuthInfoEntity.builder()
                    .oauthType(this.getOAuthType())
                    .apiKey(String.valueOf(claims.get("sub")))
                    .user(appleUser)
                    .build();

            oAuthInfoRepository.save(oAuthInfoEntity);
        }

        return UserMapper.INSTANCE.toUserResponse(appleUser);
    }

    @Override
    public OAuthType getOAuthType() throws Exception {
        return OAuthType.APPLE;
    }

    @Override
    public void revoke(String authorizationCode) throws Exception {
        String clientSecret = createClientSecret();

        RestTemplate restTemplate = new RestTemplate();

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", APPLE_CLIENT_ID);
        params.add("client_secret", clientSecret);
        params.add("token", getAppleRefreshToken(clientSecret, authorizationCode));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        restTemplate.postForEntity(APPLE_REVOKE_URI, httpEntity, String.class);
    }

    private String getAppleRefreshToken(String clientSecret, String authorizationCode) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_secret", clientSecret);
        params.add("code", authorizationCode);
        params.add("grant_type", "authorization_code");
        params.add("client_id", APPLE_CLIENT_ID);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(APPLE_TOKEN_URI, httpEntity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> body = objectMapper.readValue(response.getBody(), Map.class);

        return body.get("refresh_token").toString();
    }

    private String createClientSecret() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Date exp = Date.from(LocalDateTime.now()
                .plusDays(30)
                .atZone(ZoneId.systemDefault()).toInstant());
        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", APPLE_KEY_ID);
        jwtHeader.put("alg", "ES256");
        return Jwts.builder()
                .setHeaderParams(jwtHeader)
                .setIssuer(APPLE_TEAM_ID)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(exp)
                .setAudience("https://appleid.apple.com")
                .setSubject(APPLE_CLIENT_ID)
                .signWith(getApplePrivateKey(), SignatureAlgorithm.ES256)
                .compact();
    }

    private PrivateKey getApplePrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("EC");
        return kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(APPLE_PRIVATE_KEY)));
    }

    private Optional<Claims> verifyToken(String identityToken) throws Exception {
        try {
            PublicKeys applePublicKeys = requestApplePublicKeys();
            Key key = selectAppropriateKey(applePublicKeys, identityToken);

            byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
            byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            return Optional.ofNullable(this.validateIdToken(identityToken, publicKey, APPLE_CLIENT_ID));

        } catch (JsonProcessingException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RequestInputException(ErrorMessage.INVALID_TOKEN);
        }
    }

    private PublicKeys requestApplePublicKeys() throws JsonProcessingException {
        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> response = rt.getForEntity(APPLE_KEY_URI, String.class);

        return new ObjectMapper().readValue(response.getBody(), PublicKeys.class);
    }

    private Key selectAppropriateKey(PublicKeys keys, String idToken) throws Exception {
        List<Key> keyList = keys.getKeys();

        String headerOfIdToken = idToken.substring(0, idToken.indexOf("."));
        Map<String, String> header = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(headerOfIdToken), "UTF-8"), Map.class);

        String kid = header.get("kid");

        for (Key key : keyList) {
            if (key.getKid().equals(kid)) {
                return key;
            }
        }

        throw new RequestInputException(ErrorMessage.INVALID_TOKEN);
    }

    private Claims validateIdToken(String idToken, PublicKey key, String aud) throws Exception {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(idToken)
                    .getBody();

            return claims.get("aud").toString().equals(aud) ? claims : null;
        } catch (ExpiredJwtException expiredJwtException) {
            throw new RequestInputException(ErrorMessage.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new RequestInputException(ErrorMessage.INVALID_TOKEN);
        }
    }

}