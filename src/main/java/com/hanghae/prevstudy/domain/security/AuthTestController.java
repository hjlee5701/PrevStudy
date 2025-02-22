package com.hanghae.prevstudy.domain.security;

import com.hanghae.prevstudy.domain.member.dto.SignupRequest;
import com.hanghae.prevstudy.domain.security.dto.UserDetailsImpl;
import com.hanghae.prevstudy.global.annotation.AuthMemberInfo;
import com.hanghae.prevstudy.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/success")
    public ResponseEntity<ApiResponse<UserDetailsImpl>> success(@AuthMemberInfo UserDetailsImpl userDetails) {

        System.out.println(userDetails.getUsername());

        return ResponseEntity.ok(
                ApiResponse.success("테스트 성공", userDetails));
    }


}
