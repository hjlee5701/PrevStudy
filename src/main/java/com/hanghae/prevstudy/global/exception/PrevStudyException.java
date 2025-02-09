package com.hanghae.prevstudy.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PrevStudyException extends RuntimeException {
    private final String errCode;
    private final String message;
    private final HttpStatus status;

    public PrevStudyException(BoardErrorCode boardErrorCode) {
        super(boardErrorCode.getMessage());
        this.errCode = boardErrorCode.getErrCode();
        this.message = boardErrorCode.getMessage();
        this.status = boardErrorCode.getStatus();
    }

}
