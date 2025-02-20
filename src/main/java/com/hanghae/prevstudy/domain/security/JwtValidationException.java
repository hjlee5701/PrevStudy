package com.hanghae.prevstudy.domain.security;

import com.hanghae.prevstudy.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class JwtValidationException extends RuntimeException {
    private final String message;
    private final HttpStatus httpStatus;

    public JwtValidationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.message = errorCode.getMessage();
        this.httpStatus = errorCode.getHttpStatus();
    }
}
