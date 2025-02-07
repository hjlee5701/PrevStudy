package com.hanghae.prevstudy.domain.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Test
    @DisplayName("BoardRepository_널_체크")
    void BoardRepository_isNull() {
        assertThat(boardRepository).isNotNull();
    }

    @Test
    @DisplayName("게시글_저장")
    void 게시글_저장() {
        Board board = new Board("제목", "작성자", "내용", "비밀번호");
        Board newBoard = boardRepository.save(board);
        assertThat(board).isEqualTo(newBoard);
    }
}

