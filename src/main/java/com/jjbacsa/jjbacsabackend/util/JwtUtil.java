package com.jjbacsa.jjbacsabackend.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${jwt.key}")
    private String key;

    public String generateToken(Long id){
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        Map<String, Object> payloads = new HashMap<String, Object>();
        payloads.put("id", id);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        //TODO: refresh token 생성 구분
        calendar.add(Calendar.HOUR_OF_DAY, 12);

        Date exp = calendar.getTime();

        //TODO: signWith에 key 사용
        return Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();
    }
}
