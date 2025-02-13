package com.hanghae.prevstudy.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;


    public static <T> ApiResponse<T> success (String message, T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), message, data);
    }

}
