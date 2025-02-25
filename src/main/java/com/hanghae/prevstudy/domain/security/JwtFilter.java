package com.hanghae.prevstudy.domain.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.prevstudy.domain.security.service.TokenProvider;
import com.hanghae.prevstudy.global.exception.JwtValidationException;
import com.hanghae.prevstudy.global.response.ApiResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 토큰 추출
        String token = extractToken(request);

        if (token == null || token.trim().isEmpty()) {
            // 토큰이 없기 때문에 SecurityContext 설정이 안된다.
            // 만약 해당 요청이 보호된 리소스에 대한 요청이라면 AuthenticationEntryPoint 에서 처리된다.
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 토큰 유효성 검사 및 사용자 정보 추출
            Claims claims = tokenProvider.parseToken(token);

            // security context 저장
            setSecurityContext(claims.getSubject());

            filterChain.doFilter(request, response);

        } catch (JwtValidationException ex) {
            sendJsonErrorResponse(response, ex.getHttpStatus().value(), ex.getMessage());
        } catch (UsernameNotFoundException e) {
            sendJsonErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }


    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            return token.isEmpty() ? null : token;
        }
        return null;
    }

    private void setSecurityContext(String memberId) throws UsernameNotFoundException {
        // 사용자 정보 조회
        UserDetails userDetails = userDetailsService.loadUserByUsername(memberId);

        // 인증 객체 생성
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // 컨텍스트에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    // 공통 JSON 응답 처리 메서드
    private void sendJsonErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        new ObjectMapper().writeValue(response.getWriter(),
                ApiResponse.error(status, message));

    }
}
