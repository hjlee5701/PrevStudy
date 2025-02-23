package com.hanghae.prevstudy.domain.security;

import com.hanghae.prevstudy.domain.member.dto.SignupRequest;
import com.hanghae.prevstudy.domain.security.dto.UserDetailsImpl;
import com.hanghae.prevstudy.global.annotation.AuthMemberInfo;
import com.hanghae.prevstudy.global.resolver.AuthMemberDto;
import com.hanghae.prevstudy.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class AuthTestController {

    @GetMapping("/error")
    public void error(@AuthMemberInfo SignupRequest userDetails) {
    }

    @GetMapping("/pass")
    public void error(@AuthMemberInfo AuthMemberDto authMemberDto) {
    }

    @GetMapping("/success")
    public ResponseEntity<ApiResponse<AuthMemberDto>> success(@AuthMemberInfo AuthMemberDto authMemberDto) {


        return ResponseEntity.ok(
                ApiResponse.success("테스트 성공", authMemberDto));
    }


}
