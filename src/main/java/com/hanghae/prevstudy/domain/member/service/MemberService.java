package com.hanghae.prevstudy.domain.member.service;

import com.hanghae.prevstudy.domain.member.dto.AuthResultDto;
import com.hanghae.prevstudy.domain.member.dto.LoginRequest;
import com.hanghae.prevstudy.domain.member.dto.SignupRequest;

public interface MemberService {
    void signup(SignupRequest signupRequest);

    AuthResultDto login(LoginRequest loginRequest);
}
