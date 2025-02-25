package com.hanghae.prevstudy.domain.member.controller;

import com.hanghae.prevstudy.domain.member.dto.AuthResultDto;
import com.hanghae.prevstudy.domain.member.dto.LoginRequest;
import com.hanghae.prevstudy.domain.member.dto.LoginResponse;
import com.hanghae.prevstudy.domain.member.dto.SignupRequest;
import com.hanghae.prevstudy.domain.member.service.MemberService;
import com.hanghae.prevstudy.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class MemberController {

    private final MemberService memberService;
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        memberService.signup(signupRequest);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", null));
    }

    // TODO 수정
    @GetMapping
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResultDto authResult = memberService.login(loginRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authResult.getTokenDto().getAccessToken());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success("로그인 성공", authResult.getLoginResponse()));
    }
}
