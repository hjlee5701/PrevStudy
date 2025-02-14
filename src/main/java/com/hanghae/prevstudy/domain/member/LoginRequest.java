package com.hanghae.prevstudy.domain.member;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequest {

    @Pattern(
            regexp = "^[a-z0-9]{4,10}$",
            message = "사용자 이름은 4자 이상, 10자 이하의 알파벳 소문자 및 숫자로 구성되어야 합니다."
    )
    private final String username;

    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "비밀번호는 8자 이상, 15자 이하이며 알파벳 대소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private final String password;
}

