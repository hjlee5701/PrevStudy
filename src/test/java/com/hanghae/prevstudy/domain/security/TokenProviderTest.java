package com.hanghae.prevstudy.domain.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TokenProviderTest {

    private TokenProvider tokenProvider;
    private String secret;

    @BeforeEach
    void setInit() throws Exception {
        secret = "c2VjcmV0LTEtU2Vc2VjcmV0LTEtU2VjcmV0LTIjcmV0LTL";
        long tokenValidityInMilliseconds = 86400;

        tokenProvider = new TokenProvider(secret, tokenValidityInMilliseconds);
        tokenProvider.afterPropertiesSet();
    }
    @Test
    @DisplayName("토큰_발급")
    void 토큰_발급() {
        // given
        String memberId = "1";

        // when
        TokenDto tokenDto = tokenProvider.createToken(memberId);

        // then
        assertThat(tokenDto).isNotNull();
    }
}