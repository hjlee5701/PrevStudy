package com.hanghae.prevstudy.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements ErrorCode {
    FAIL_GET_BOARD("B001", "게시글 상세 조회 실패")
    ;

    private final String errCode;
    private final String message;

}
