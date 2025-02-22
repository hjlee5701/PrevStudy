package com.hanghae.prevstudy.domain.security;

import com.hanghae.prevstudy.domain.member.dto.SignupRequest;
import com.hanghae.prevstudy.global.AuthMemberInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class AuthTestController {

    @GetMapping("/error")
    public void error(@AuthMemberInfo SignupRequest userDetails) {
    }

    @GetMapping("/pass")
    public void error(@AuthMemberInfo UserDetailsImpl userDetails) {
    }


}
