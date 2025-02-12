package com.hanghae.prevstudy.global.exception;

import com.hanghae.prevstudy.domain.board.exception.BoardErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PrevStudyException extends RuntimeException {
    private final String errCode;
    private final String message;
    private final HttpStatus status;

    public PrevStudyException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errCode = errorCode.getErrCode();
        this.message = errorCode.getMessage();
        this.status = errorCode.getStatus();
    }

}
