package com.hanghae.prevstudy.domain.member.exception;

import com.hanghae.prevstudy.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    DUPLICATE_USERNAME("중복된 username 입니다.", HttpStatus.BAD_REQUEST),
    FAILED_LOGIN("회원을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_AUTH_MEMBER_TYPE("서버 설정 오류입니다. 관리자에게 문의하세요.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED_ACCESS("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
    ;

    private final String message;
    private final HttpStatus httpStatus;
}
