package com.jjbacsa.jjbacsabackend.etc.filter;

import com.jjbacsa.jjbacsabackend.etc.config.RepeatableRequestWrapper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestWrappingFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        RepeatableRequestWrapper repeatableRequestWrapper = new RepeatableRequestWrapper(request);
        filterChain.doFilter(repeatableRequestWrapper, response);

    }
}
