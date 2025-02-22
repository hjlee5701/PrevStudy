package com.hanghae.prevstudy.domain.board.exception;

import com.hanghae.prevstudy.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardErrorCode implements ErrorCode {
    BOARD_NOT_FOUND("게시글이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    FORBIDDEN_ACCESS("게시글에 대한 접근 권한이 없습니다.", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus httpStatus;

}
