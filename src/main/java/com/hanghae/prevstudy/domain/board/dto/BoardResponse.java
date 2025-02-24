package com.hanghae.prevstudy.domain.board.dto;

import com.hanghae.prevstudy.domain.comment.dto.CommentResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;

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

    private final List<CommentResponse> comment;
}
