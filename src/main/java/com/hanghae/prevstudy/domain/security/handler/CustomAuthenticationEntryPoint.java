package com.hanghae.prevstudy.domain.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.prevstudy.global.exception.errorCode.JwtErrorCode;
import com.hanghae.prevstudy.global.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        new ObjectMapper().writeValue(response.getWriter(),
                ApiResponse.error(401, JwtErrorCode.JWT_NO_HEADER.getMessage()));
    }
}
