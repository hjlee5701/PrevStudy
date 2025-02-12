package com.hanghae.prevstudy.domain.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TokenDto {
    private final String accessToken;
    private final String refreshToken;
}
