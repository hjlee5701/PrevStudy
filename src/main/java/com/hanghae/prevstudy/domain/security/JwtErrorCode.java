package com.hanghae.prevstudy.domain.security;

import com.hanghae.prevstudy.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {
    JWT_MALFORMED("올바르지 않은 JWT 형식입니다.", HttpStatus.BAD_REQUEST),
    JWT_INVALID_SIGNATURE("JWT 서명이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    JWT_EXPIRED("만료된 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_UNSUPPORTED("지원하지 않는 JWT 토큰입니다.", HttpStatus.BAD_REQUEST),
    JWT_NO_HEADER("JWT 토큰이 제공되지 않았습니다.", HttpStatus.BAD_REQUEST),

    ;


    private final String message;
    private final HttpStatus httpStatus;
}
