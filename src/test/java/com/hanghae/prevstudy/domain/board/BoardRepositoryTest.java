package com.hanghae.prevstudy.domain.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
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

    private Board newBoard() {
        return Board.builder()
                .title("제목")
                .writer("작성자")
                .content("내용")
                .password("비밀번호")
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
        Optional<Board> findBoard = boardRepository.findById(savedBoard.getId());

        // then
        assertThat(findBoard).isNotEmpty();

        assertThat(findBoard.get().getId()).isEqualTo(savedBoard.getId());
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

    @Test
    @DisplayName("게시글_수정_실패")
    void 게시글_수정_실패() {
        // given
        Long inValidBoardId = 999L;

        // when
        Optional<Board> board = boardRepository.findById(inValidBoardId);

        // then
        assertThat(board).isEmpty();
    }

    @Test
    void 게시글_수정_성공() {
        // given
        Board savedBoard = boardRepository.save(newBoard());
        Long requestBoardId = savedBoard.getId();

        Optional<Board> findBoard = boardRepository.findById(requestBoardId);
        assertThat(findBoard).isNotEmpty();

        assertThat(findBoard.get().getId()).isEqualTo(savedBoard.getId()); // ID 검증
        assertThat(findBoard.get().getTitle()).isEqualTo("제목");
        assertThat(findBoard.get().getPassword()).isEqualTo("비밀번호");

        // when
        findBoard.get().update("제목2", "내용2");

        // then
        Board updatedBoard = boardRepository.findById(requestBoardId).orElseThrow();

        assertThat(updatedBoard.getId()).isEqualTo(savedBoard.getId()); // ID 검증
        assertThat(updatedBoard.getTitle()).isEqualTo("제목2");
        assertThat(updatedBoard.getContent()).isEqualTo("내용2");
    }

    @Test
    @DisplayName("게시글_수정일자_업데이트")
    void 게시글_수정일자_업데이트() {
        // given
        Board savedBoard = boardRepository.save(newBoard());
        Long requestBoardId = savedBoard.getId();

        Board boardUpdateBoard = boardRepository.findById(requestBoardId).orElse(null);
        assertThat(boardUpdateBoard).isNotNull();

        Date beforeUpdateAt = boardUpdateBoard.getModAt();

        assertThat(beforeUpdateAt).isEqualTo(boardUpdateBoard.getRegAt()); // 최초 등록시간과 동일한지 확인

        // when
        boardUpdateBoard.update("제목2", "내용2"); // Dirty Checking 적용
        boardRepository.flush();

        Board updatedBoard = boardRepository.findById(requestBoardId).orElse(null);
        assertThat(updatedBoard).isNotNull();

        // then
        assertThat(updatedBoard.getModAt()).isNotEqualTo(beforeUpdateAt);
    }


}

