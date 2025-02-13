package com.hanghae.prevstudy.global.exception;

import com.hanghae.prevstudy.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        int status = HttpStatus.BAD_REQUEST.value();

        return new ResponseEntity<>(ApiResponse.error(status, message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PrevStudyException.class)
    public ResponseEntity<?> handleCustomException(PrevStudyException ex) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(ex.getErrCode(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }
}
