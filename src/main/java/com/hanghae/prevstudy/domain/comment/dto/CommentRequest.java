package com.hanghae.prevstudy.domain.comment.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentRequest {

    @NotBlank(message = "내용을 입력해 주세요.")
    private final String content;
}
