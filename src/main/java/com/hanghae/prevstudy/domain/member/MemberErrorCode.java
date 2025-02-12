package com.hanghae.prevstudy.domain.member;

import com.hanghae.prevstudy.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    DUPLICATE_USERNAME("M001", "중복된 username 입니다.", HttpStatus.BAD_REQUEST),
    FAILED_LOGIN("M002", "사용자 로그인에 실패했습니다.", HttpStatus.BAD_REQUEST);

    private final String errCode;
    private final String message;
    private final HttpStatus status;
}
