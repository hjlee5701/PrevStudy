package com.hanghae.prevstudy.global.exception.errorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {
    COMMENT_NOT_FOUND("댓글이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    FORBIDDEN_ACCESS("댓글에 대한 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    ;
    private final String message;
    private final HttpStatus httpStatus;
}


