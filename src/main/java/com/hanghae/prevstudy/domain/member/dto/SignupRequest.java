package com.hanghae.prevstudy.domain.member.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequest {

    @Pattern(
            regexp = "^[a-z0-9]{4,10}$",
            message = "사용자 이름은 4자 이상, 10자 이하의 알파벳 소문자 및 숫자로 구성되어야 합니다."
    )
    private String username;

    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "비밀번호는 8자 이상, 15자 이하이며 알파벳 대소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    private final Boolean isAdmin;

    public SignupRequest(String username, String password, Boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin != null ? isAdmin : false;
    }

}
