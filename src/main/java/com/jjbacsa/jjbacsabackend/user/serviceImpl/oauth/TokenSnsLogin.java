package com.jjbacsa.jjbacsabackend.user.serviceImpl.oauth;

import com.jjbacsa.jjbacsabackend.user.dto.response.UserResponse;
import com.jjbacsa.jjbacsabackend.user.service.SnsLogin;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public abstract class TokenSnsLogin implements SnsLogin {

    protected UserResponse requestUserProfileByAccessToken(String accessToken, String profileUri) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate rt = new RestTemplate();

        if (accessToken.charAt(0) == '"') {
            accessToken = accessToken.substring(1, accessToken.length() - 1);
        }

        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = rt.exchange(
                profileUri,
                HttpMethod.GET,
                request,
                String.class
        );

        UserResponse user = this.profileParsing(response);

        return user;

    }

    abstract UserResponse profileParsing(ResponseEntity<String> response) throws Exception;
}
