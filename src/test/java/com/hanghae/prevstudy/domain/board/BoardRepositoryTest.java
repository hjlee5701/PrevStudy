package com.hanghae.prevstudy.domain.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

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
        Board board = Board.builder()
                .title("제목")
                .writer("작성자")
                .content("내용")
                .password("비밀번호")
                .build();
        Board newBoard = boardRepository.save(board);
        assertThat(board).isEqualTo(newBoard);
    }

    @Test
    @DisplayName("게시글_상세_조회")
    void 게시글_상세_조회() {
        // given
        Board board = Board.builder()
                .title("제목")
                .writer("작성자")
                .content("내용")
                .password("비밀번호")
                .build();
        Board savedBoard = boardRepository.save(board);

        // when
        Optional<Board> findBoard = boardRepository.findById(1L);

        // then
        assertThat(findBoard).isNotNull();
        assertThat(findBoard.get().getId()).isEqualTo(1L);
        assertThat(findBoard.get().getContent()).isEqualTo(savedBoard.getContent());
    }
}

