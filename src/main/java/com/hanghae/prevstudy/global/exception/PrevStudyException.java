package com.hanghae.prevstudy.global.exception;

import com.hanghae.prevstudy.global.exception.errorCode.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PrevStudyException extends RuntimeException {
    private final String message;
    private final HttpStatus httpStatus;

    public PrevStudyException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.message = errorCode.getMessage();
        this.httpStatus = errorCode.getHttpStatus();
    }

}
