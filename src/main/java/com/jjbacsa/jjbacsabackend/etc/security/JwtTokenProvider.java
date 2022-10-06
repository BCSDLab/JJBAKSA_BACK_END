package com.jjbacsa.jjbacsabackend.etc.security;

import com.jjbacsa.jjbacsabackend.etc.enums.TokenType;
import com.jjbacsa.jjbacsabackend.user.entity.CustomUserDetails;
import com.jjbacsa.jjbacsabackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtUtil jwtUtil;

    public Authentication getAuthentication(String token) throws Exception {
        Map<String, Object> payloads = jwtUtil.getPayloadsFromJwt(token);
        Long id = Long.parseLong(String.valueOf(payloads.get("id")));

        String authority = String.valueOf(payloads.get("auth"));
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        Arrays.stream(authority.split(","))
                .forEach(auth -> authorities.add(new SimpleGrantedAuthority(auth)));

        return new UsernamePasswordAuthenticationToken(new CustomUserDetails(id), "", authorities);
    }

    public boolean validateToken(String token, TokenType tokenType) throws Exception {
        return jwtUtil.isValid(token, tokenType);
    }
}
