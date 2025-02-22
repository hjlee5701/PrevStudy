package com.hanghae.prevstudy.domain.security;

import com.hanghae.prevstudy.domain.security.dto.TokenDto;
import com.hanghae.prevstudy.domain.security.service.TokenProvider;
import com.hanghae.prevstudy.global.exception.JwtValidationException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TokenProviderTest {

    private TokenProvider tokenProvider;
    private final String VALID_SECRET_KEY = "c2VjcmV0LTEtU2Vc2VjcmV0LTEtU2VjcmV0LTIjcmV0LTL";


    void setTokenProvider(String secretKey, long accessExpSec, long refreshExpSec) {

        tokenProvider = new TokenProvider(secretKey, accessExpSec, refreshExpSec);
        tokenProvider.init();
    }

    @Test
    @DisplayName("토큰_발급_성공")
    void 토큰_발급_성공() {
        // given
        String memberId = "1";
        setTokenProvider(VALID_SECRET_KEY, 300, 300);

        // when
        TokenDto tokenDto = tokenProvider.createToken(memberId);

        // then
        assertThat(tokenDto).isNotNull();
        assertTrue(tokenDto.getAccessToken().matches("^[a-zA-Z0-9\\-_.]+$"));
        assertTrue(tokenDto.getRefreshToken().matches("^[a-zA-Z0-9\\-_.]+$"));

        assertTrue(tokenDto.getAccessToken().length() > 20);
        assertTrue(tokenDto.getRefreshToken().length() > 20);
    }


    @Test
    @DisplayName("올바르지 형식 토큰 예외 테스트")
    void 토큰_형식_에러() {
        // given
        setTokenProvider(VALID_SECRET_KEY, 300, 0);

        // when
        JwtValidationException unSupportException
                = assertThrows(JwtValidationException.class, () -> tokenProvider.parseToken("not-valid-token"));
        // then
        assertEquals("올바르지 않은 JWT 형식입니다.", unSupportException.getMessage());
    }

    @Test
    @DisplayName("서명 에러 토큰 예외 테스트")
    void 서명_에러_토큰() {
        // given
        setTokenProvider(VALID_SECRET_KEY, 300, 0);
        String token = tokenProvider.createToken("1").getAccessToken();

        // when
        setTokenProvider("In" + VALID_SECRET_KEY, 300, 0);
        JwtValidationException expireException = assertThrows(JwtValidationException.class, () -> tokenProvider.parseToken(token));

        // then
        assertEquals("JWT 서명이 유효하지 않습니다.", expireException.getMessage());
    }

    @Test
    @DisplayName("만료된 토큰 예외 테스트")
    void 만료된_토큰() {
        // given
        setTokenProvider(VALID_SECRET_KEY, 0, 0);
        String expiredToken = tokenProvider.createToken("1").getAccessToken();

        // when
        JwtValidationException expireException = assertThrows(JwtValidationException.class, () -> tokenProvider.parseToken(expiredToken));

        // then
        assertEquals("만료된 JWT 토큰입니다.", expireException.getMessage());
    }

    @Test
    @DisplayName("토큰_분석_성공")
    void 토큰_분석_성공() {
        // given
        setTokenProvider(VALID_SECRET_KEY, 300, 0);
        String validToken = tokenProvider.createToken("1").getAccessToken();

        // when
        Claims claims = tokenProvider.parseToken(validToken);

        // then
        assertThat(claims.getSubject()).isEqualTo("1");
        assertTrue(claims.getExpiration().after(new Date()));
    }
}