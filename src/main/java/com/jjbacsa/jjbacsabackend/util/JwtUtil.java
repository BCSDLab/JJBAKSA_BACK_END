package com.jjbacsa.jjbacsabackend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.etc.enums.TokenType;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtUtil {
    @Value("${jwt.key}")
    private String key;

    @Value("${jwt.access}")
    private String accessToken;

    @Value("${jwt.refresh}")
    private String refreshToken;

    public static final short BEARER_LENGTH = 7;

    public String generateToken(String account, TokenType type){
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        Map<String, Object> payloads = new HashMap<String, Object>();
        payloads.put("account", account);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        //TODO: refresh token 생성 구분
        calendar.add(Calendar.HOUR_OF_DAY, type.getTokenRemainTime());

        Date exp = calendar.getTime();

        //TODO: signWith에 key 사용
        return Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setSubject(type.isAccess() ? accessToken : refreshToken)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();
    }

    //TODO: refresh token 추가 로직 적용
    public boolean isValid(String token, TokenType tokenType) throws Exception {
        if(token == null){
            throw new Exception("Token is null");
        }
        if(token.length() < BEARER_LENGTH + 1){
            throw new Exception("Not Invalid Token");
        }
        if(!token.startsWith("Bearer ")){
            throw new Exception("Token is Not Bearer");
        }

        Claims claims = null;
        try{
            claims = Jwts.parserBuilder().setSigningKey(
                    key.getBytes())
                    .build()
                    .parseClaimsJws(token.substring(BEARER_LENGTH))
                    .getBody();
        } catch (ExpiredJwtException expiredJwtException){
            throw expiredJwtException;
        }  catch (JwtException e){
            throw new Exception("Token Invalid");
        }

        if(claims.getSubject() == null || claims.get("account", String.class) == null){
            throw new Exception("Token Invalid");
        }

        if(!claims.getSubject().equals(tokenType.isAccess()? accessToken:refreshToken)){
            throw new Exception("Token Type Invalid");
        }

        return true;
    }

    public Map<String, Object> getPayloadsFromJwt(String token) throws Exception {
        String[] chunks = token.split("\\.");
        String payloads = new String(Base64.getDecoder().decode(chunks[1]));

        HashMap<String, Object> map = null;
        try {
            map = new ObjectMapper().readValue(payloads, HashMap.class);
        } catch (JsonProcessingException e) {
            throw new Exception("Token Invalid");
        }
        return map;
    }
}
