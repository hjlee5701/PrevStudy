package com.hanghae.prevstudy.domain.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    private Board newBoard() {
        return Board.builder()
                .title("제목")
                .writer("작성자")
                .content("내용")
                .password("비밀번호")
                .regAt(new Date())
                .build();
    }
    @Test
    @DisplayName("게시글_저장")
    void 게시글_저장() {
        final Board board = newBoard();
        Board newBoard = boardRepository.save(board);
        assertThat(board).isEqualTo(newBoard);
    }

    @Test
    @DisplayName("게시글_상세_조회")
    void 게시글_상세_조회() {
        // given
        final Board board = newBoard();
        Board savedBoard = boardRepository.save(board);

        // when
        Optional<Board> findBoard = boardRepository.findById(1L);

        // then
        assertThat(findBoard).isNotNull();
        assertThat(findBoard.get().getId()).isEqualTo(1L);
        assertThat(findBoard.get().getContent()).isEqualTo(savedBoard.getContent());
    }

    @Test
    @DisplayName("게시글_전체_조회")
    void 게시글_전체_조회() {
        // TODO 페이징 처리
        //given
        Board board1 = newBoard();
        Board board2 = newBoard();
        Board board3 = newBoard();

        boardRepository.save(board1);
        boardRepository.save(board2);
        boardRepository.save(board3);

        // when
        List<Board> findAllBoards = boardRepository.findAll();

        // then
        assertThat(findAllBoards).isNotEmpty();
        assertThat(findAllBoards.size()).isEqualTo(3);
    }
}

