package com.hanghae.prevstudy.domain.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberAddRequest {
    private final String username;
    private final String password;
}
