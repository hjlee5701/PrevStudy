package com.hanghae.prevstudy.domain.board;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BoardAddRequest {
    private final String title;
    private final String writer;
    private final String content;
    private final String password;
}
