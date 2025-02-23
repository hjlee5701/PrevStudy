package com.hanghae.prevstudy.domain.comment.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentAddRequest {
    private final String content;
}
