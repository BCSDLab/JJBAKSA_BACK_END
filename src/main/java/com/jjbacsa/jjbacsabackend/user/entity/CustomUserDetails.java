package com.jjbacsa.jjbacsabackend.user.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomUserDetails implements UserDetails, OAuth2User {
    private Long id;
    private Map<String, Object> attributes;

    public CustomUserDetails(Long id){
        this.id = id;
    }

    public CustomUserDetails(Long id, Map<String, Object> attributes) {
        this.id = id;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return null;
    }

    @Override
    public String getPassword(){
        return null;
    }

    @Override
    public String getUsername(){
        return String.valueOf(id);
    }

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }

    @Override
    public boolean isEnabled(){
        return false;
    }

    public Long getId(){ return id; }

    @Override
    public String getName() {
        return String.valueOf(id);
    }

}
