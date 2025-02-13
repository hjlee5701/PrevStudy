package com.hanghae.prevstudy.domain.member;

import com.hanghae.prevstudy.domain.security.TokenDto;

public interface MemberService {
    void signup(MemberAddRequest memberAddRequest);

    TokenDto login(LoginRequest loginRequest);
}
