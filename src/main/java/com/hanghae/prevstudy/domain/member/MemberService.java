package com.hanghae.prevstudy.domain.member;

public interface MemberService {
    void signup(MemberAddRequest memberAddRequest);

    AuthResultDto login(LoginRequest loginRequest);
}
