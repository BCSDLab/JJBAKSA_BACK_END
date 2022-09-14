package com.jjbacsa.jjbacsabackend.etc.interceptor;

import com.jjbacsa.jjbacsabackend.etc.annotations.Auth;
import com.jjbacsa.jjbacsabackend.etc.enums.TokenType;
import com.jjbacsa.jjbacsabackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Auth auth= handlerMethod.getMethod().getDeclaredAnnotation(Auth.class);

        if(auth == null){
            return true;
        }

        String accessToken = request.getHeader("Authorization");
        if(accessToken == null){
            throw new Exception("Access Token is Null");
        }

        return jwtUtil.isValid(accessToken, TokenType.ACCESS);
    }

}
