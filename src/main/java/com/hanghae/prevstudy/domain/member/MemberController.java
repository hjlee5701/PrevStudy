package com.hanghae.prevstudy.domain.member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class MemberController {

    private final MemberService memberService;
    @PostMapping
    public ResponseEntity<String> signup(@Valid @RequestBody MemberAddRequest memberAddRequest) {
        memberService.signup(memberAddRequest);
        return ResponseEntity.ok("회원가입 완료");
    }
}
