package com.hanghae.prevstudy.domain.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class CommentResponse {

    private final Long commentId;
    private final String writer;
    private final String content;
}
