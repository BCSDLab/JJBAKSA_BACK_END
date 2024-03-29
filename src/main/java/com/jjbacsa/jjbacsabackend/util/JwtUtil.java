package com.jjbacsa.jjbacsabackend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.enums.TokenType;
import com.jjbacsa.jjbacsabackend.etc.exception.RequestInputException;
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

    public String generateToken(Long id, TokenType type, String authority){
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        Map<String, Object> payloads = new HashMap<String, Object>();
        payloads.put("id", id);
        payloads.put("auth", authority);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

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
            throw new RequestInputException(ErrorMessage.INVALID_TOKEN);
        }
        if(token.length() < BEARER_LENGTH + 1) {
            throw new RequestInputException(ErrorMessage.INVALID_TOKEN);
        }
        if(!token.startsWith("Bearer ")){
            throw new RequestInputException(ErrorMessage.INVALID_TOKEN);
        }

        Claims claims = null;
        try{
            claims = Jwts.parserBuilder().setSigningKey(
                    key.getBytes())
                    .build()
                    .parseClaimsJws(token.substring(BEARER_LENGTH))
                    .getBody();
        } catch (ExpiredJwtException expiredJwtException){
            throw new RequestInputException(ErrorMessage.EXPIRED_TOKEN);
        }  catch (JwtException e){
            throw new RequestInputException(ErrorMessage.INVALID_TOKEN);
        }

        if(claims.getSubject() == null || claims.get("id", Long.class) == null){
            throw new RequestInputException(ErrorMessage.INVALID_TOKEN);
        }

        if(!claims.getSubject().equals(tokenType.isAccess()? accessToken:refreshToken)){
            throw new RequestInputException(ErrorMessage.INVALID_TOKEN_TYPE);
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
            throw new RequestInputException(ErrorMessage.INVALID_TOKEN);
        }
        return map;
    }
}
