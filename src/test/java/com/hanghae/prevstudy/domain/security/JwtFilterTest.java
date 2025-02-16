package com.hanghae.prevstudy.domain.security;

import com.hanghae.prevstudy.global.exception.PrevStudyException;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static com.hanghae.prevstudy.domain.security.JwtErrorCode.JWT_UNSUPPORTED;
import static org.junit.jupiter.api.Assertions.*;

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

}
