package com.jjbacsa.jjbacsabackend.etc.filter;

import com.jjbacsa.jjbacsabackend.etc.security.CustomCorsConfigSource;
import org.springframework.web.cors.*;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CustomCorsFilter extends OncePerRequestFilter {

    private CorsProcessor processor = new DefaultCorsProcessor();
    private final CorsConfigurationSource configSource;

    public CustomCorsFilter(CustomCorsConfigSource customCorsConfigSource){
        this.configSource = customCorsConfigSource.getCorsConfigurationSource();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        CorsConfiguration corsConfiguration = this.configSource.getCorsConfiguration(request);
        boolean isValid = this.processor.processRequest(corsConfiguration, request, response);
        if (!isValid) {
            return;
        }
        else if(CorsUtils.isPreFlightRequest(request)) {
            response.setStatus(HttpServletResponse.SC_OK);
        }
        else filterChain.doFilter(request, response);
    }

}