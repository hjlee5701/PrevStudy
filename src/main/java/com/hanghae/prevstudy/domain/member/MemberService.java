package com.hanghae.prevstudy.domain.member;

public interface MemberService {
    void signup(MemberAddRequest memberAddRequest);

    void login(LoginRequest loginRequest);
}
