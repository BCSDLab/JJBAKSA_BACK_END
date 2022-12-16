package com.jjbacsa.jjbacsabackend.annotation.CustomSecurityContext;

import com.jjbacsa.jjbacsabackend.annotation.WithMockCustomUser;
import com.jjbacsa.jjbacsabackend.user.entity.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser>{
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        UserDetails userDetails = new CustomUserDetails(Long.valueOf(annotation.id()));

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        Arrays.stream(annotation.role().getUserType().split(","))
                .forEach(auth -> authorities.add(new SimpleGrantedAuthority(auth)));

        final UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(userDetails,
                "",
                authorities);

        securityContext.setAuthentication(authenticationToken);
        return securityContext;
    }
}
