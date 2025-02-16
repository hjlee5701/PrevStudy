package com.hanghae.prevstudy.domain.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

    @InjectMocks
    private JwtFilter jwtFilter;

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

}
