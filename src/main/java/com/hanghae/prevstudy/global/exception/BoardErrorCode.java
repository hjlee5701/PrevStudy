package com.hanghae.prevstudy.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements ErrorCode {
    FAIL_GET_BOARD("B001", "게시글이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD("B002", "입력한 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String errCode;
    private final String message;
    private final HttpStatus status;

}
