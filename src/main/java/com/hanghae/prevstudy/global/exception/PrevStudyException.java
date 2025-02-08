package com.hanghae.prevstudy.global.exception;

import lombok.Getter;

@Getter
public class PrevStudyException extends RuntimeException {
    private final String errCode;
    private final String message;

    public PrevStudyException(BoardErrorCode boardErrorCode) {
        super(boardErrorCode.getMessage());
        this.errCode = boardErrorCode.getErrCode();
        this.message = boardErrorCode.getMessage();
    }

}
