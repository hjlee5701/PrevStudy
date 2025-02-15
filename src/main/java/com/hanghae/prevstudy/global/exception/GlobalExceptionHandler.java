package com.hanghae.prevstudy.global.exception;

import com.hanghae.prevstudy.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {

        List<String> messages = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())  // 필드명 + 오류 메시지
                .toList();

        String message = messages.toString();

        int status = HttpStatus.BAD_REQUEST.value();
        return ResponseEntity.badRequest().body(ApiResponse.error(status, message));
    }


    @ExceptionHandler(PrevStudyException.class)
    public ResponseEntity<?> handleCustomException(PrevStudyException ex) {

        return new ResponseEntity<>(
                ApiResponse.error(ex.getHttpStatus().value(), ex.getMessage()), ex.getHttpStatus()
        );
    }
}
