package com.hanghae.prevstudy.domain.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.Map;

import static com.hanghae.prevstudy.domain.security.JwtErrorCode.JWT_UNSUPPORTED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

    @InjectMocks
    private JwtFilter jwtFilter;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("토큰_추출_실패")
    void 토큰_추출_실패() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest(); // Filter 에서 행위 검증(@Mock)이 아닌 응답 형태 검증 용이
        request.addHeader("Authorization", "Bearer12345");

        // when
        String token = jwtFilter.extractToken(request);

        // then
        assertNull(token);
    }

    @Test
    @DisplayName("토큰_추출_성공")
    void 토큰_추출_성공() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer 12345");

        // when
        String token = jwtFilter.extractToken(request);

        // then
        assertNotNull(token);

    }

    @Test
    @DisplayName("HttpServletResponse에_JwtValidationException_설정")
    void HttpServletResponse에_JwtValidationException_설정() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        BDDMockito.given(tokenProvider.parseToken(null))
                .willThrow(new JwtValidationException(JWT_UNSUPPORTED));

        // when
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        assertEquals(JWT_UNSUPPORTED.getMessage(), response.getErrorMessage());
        assertEquals(JWT_UNSUPPORTED.getHttpStatus().value(), response.getStatus());
    }

    @Test
    @DisplayName("HttpServletResponse에_UsernameNotFoundException_설정")
    void HttpServletResponse에_UsernameNotFoundException_설정() throws ServletException, IOException {
        // given
        MockHttpServletResponse response = new MockHttpServletResponse();

        BDDMockito.given(tokenProvider.parseToken(Mockito.any()))
                .willReturn(new DefaultClaims(Map.of("sub", "1")));

        BDDMockito.given(userDetailsService.loadUserByUsername(anyString()))
                .willThrow(new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // when
        jwtFilter.doFilterInternal(
                new MockHttpServletRequest(),
                response,
                new MockFilterChain()
        );

        // then
        assertEquals("사용자를 찾을 수 없습니다.", response.getErrorMessage());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    @DisplayName("유효한 JWT 토큰이 주어졌을 때 SecurityContext에 Authentication이 저장된다.")
    void SecurityContext에_인증_정보_저장_성공()
            throws ServletException, IOException {
        // given
        setUpContext(); // security context 설정

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-access-token");

        Claims claims = new DefaultClaims(Map.of("sub", "1"));
        BDDMockito.given(tokenProvider.parseToken(anyString()))
                .willReturn(claims);

        BDDMockito.given(userDetailsService.loadUserByUsername(anyString()))
                .willReturn(new UserDetailsImpl(1L, "회원", "비밀번호", null));

        // when
        jwtFilter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertNotNull(authentication.getPrincipal());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        assertEquals(1L, userDetails.getId());
        assertEquals("회원", userDetails.getUsername());
        assertEquals("비밀번호", userDetails.getPassword());

        tearDownContext(); // security context 설정
    }

    void setUpContext() {
        SecurityContext testContext = SecurityContextHolder.createEmptyContext(); // 테스트 격리 보장
        SecurityContextHolder.setContext(testContext);
    }
    void tearDownContext() {
        SecurityContextHolder.clearContext();
    }


}
