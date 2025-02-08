package com.hanghae.prevstudy.domain.board;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BoardAddRequest {

    @NotBlank(message = "제목을 입력해 주세요.")
    private final String title;

    @NotBlank(message = "작성자를 입력해 주세요.")
    private final String writer;

    @NotBlank(message = "내용을 입력해 주세요.")
    private final String content;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private final String password;
}
