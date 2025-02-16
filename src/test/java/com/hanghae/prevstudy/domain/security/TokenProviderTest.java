package com.hanghae.prevstudy.domain.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

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
}