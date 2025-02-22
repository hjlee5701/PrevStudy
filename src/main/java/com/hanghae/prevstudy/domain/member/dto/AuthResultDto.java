package com.hanghae.prevstudy.domain.member.dto;

import com.hanghae.prevstudy.domain.security.dto.TokenDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResultDto {
    private final TokenDto tokenDto;
    private final LoginResponse loginResponse;
}
