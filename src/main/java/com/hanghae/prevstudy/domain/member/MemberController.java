package com.hanghae.prevstudy.domain.member;

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
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody MemberAddRequest memberAddRequest) {
        memberService.signup(memberAddRequest);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", null));
    }

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
