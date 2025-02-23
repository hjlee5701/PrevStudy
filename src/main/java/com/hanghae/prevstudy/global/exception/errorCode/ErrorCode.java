package com.hanghae.prevstudy.global.exception.errorCode;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String getMessage();
    HttpStatus getHttpStatus();

}
