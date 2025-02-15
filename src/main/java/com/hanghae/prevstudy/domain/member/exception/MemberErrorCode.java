package com.hanghae.prevstudy.domain.member.exception;

import com.hanghae.prevstudy.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    DUPLICATE_USERNAME("중복된 username 입니다.", HttpStatus.BAD_REQUEST),
    FAILED_LOGIN("회원을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;
}
