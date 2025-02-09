package com.hanghae.prevstudy.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomErrorResponse {
    private final String errCode;
    private final String message;
}
