package com.hanghae.prevstudy.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements ErrorCode {
    FAIL_GET_BOARD("B001", "게시글 상세 조회 실패", HttpStatus.BAD_REQUEST)
    ;

    private final String errCode;
    private final String message;
    private final HttpStatus status;

}
