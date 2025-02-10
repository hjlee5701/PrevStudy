package com.hanghae.prevstudy.domain.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@Builder
@RequiredArgsConstructor
public class BoardResponse {

    private final Long boardId;
    private final String title;
    private final String writer;
    private final String content;
    private final Date regAt;
    private final Date modAt;
}
