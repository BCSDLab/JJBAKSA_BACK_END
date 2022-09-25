package com.jjbacsa.jjbacsabackend.etc.security;

import com.jjbacsa.jjbacsabackend.etc.enums.TokenType;
import com.jjbacsa.jjbacsabackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public Authentication getAuthentication(String token) throws Exception {
        Long id = Long.parseLong(String.valueOf(jwtUtil.getPayloadsFromJwt(token).get("id")));
        UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(id));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean validateToken(String token, TokenType tokenType) throws Exception {
        return jwtUtil.isValid(token, tokenType);
    }
}
