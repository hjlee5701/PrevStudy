package com.hanghae.prevstudy.global.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String getErrCode();
    String getMessage();

    HttpStatus getStatus();

}
