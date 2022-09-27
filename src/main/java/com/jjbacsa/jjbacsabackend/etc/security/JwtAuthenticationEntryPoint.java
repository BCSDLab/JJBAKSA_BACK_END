package com.jjbacsa.jjbacsabackend.etc.security;

import com.jjbacsa.jjbacsabackend.etc.exception.BaseException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        BaseException exception = (BaseException)request.getAttribute("exception");
        setResponse(response, exception);
    }

    private void setResponse(HttpServletResponse response, BaseException exception) throws IOException{
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(exception);
    }
}
