package com.hanghae.prevstudy.domain.security.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TokenDto {
    private final String memberId;
    private final String accessToken;
    private final String refreshToken;
}
