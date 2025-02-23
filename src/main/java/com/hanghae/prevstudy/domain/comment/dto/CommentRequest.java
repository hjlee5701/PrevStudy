package com.hanghae.prevstudy.domain.comment.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentRequest {
    private final String content;
}
