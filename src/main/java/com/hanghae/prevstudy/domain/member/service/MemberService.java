package com.hanghae.prevstudy.domain.member.service;

import com.hanghae.prevstudy.domain.member.dto.AuthResultDto;
import com.hanghae.prevstudy.domain.member.dto.LoginRequest;
import com.hanghae.prevstudy.domain.member.dto.MemberAddRequest;

public interface MemberService {
    void signup(MemberAddRequest memberAddRequest);

    AuthResultDto login(LoginRequest loginRequest);
}
