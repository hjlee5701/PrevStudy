package com.hanghae.prevstudy.domain.board;

import com.hanghae.prevstudy.global.exception.PrevStudyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardServiceImpl boardService;

    private Board newBoard() {
        return Board.builder()
                .id(1L)
                .title("제목")
                .writer("작성자")
                .content("내용")
                .password("비밀번호")
                .regAt(new Date())
                .build();
    }


    @Test
    @DisplayName("게시글_생성")
    void 게시글_생성() {

        // given
        assertThat(boardService).isNotNull();

        Board board = newBoard();
        doReturn(board).when(boardRepository).save(any(Board.class));

        // when
        BoardAddRequest requestBoardDto = new BoardAddRequest(board.getTitle(), board.getWriter(), board.getContent(), board.getPassword());
        final BoardResponse savedBoard = boardService.add(requestBoardDto);

        // then
        assertThat(savedBoard).isNotNull();
        assertThat(savedBoard.getTitle()).isEqualTo(requestBoardDto.getTitle());
        assertThat(savedBoard.getContent()).isEqualTo(requestBoardDto.getContent());
        verify(boardRepository, times(1)).save(any(Board.class));
    }

    @Test
    @DisplayName("게시글_상세_조회_실패")
    void 게시글_상세_조회_실패() {
        // given
        doReturn(Optional.empty()).when(boardRepository).findById(any(Long.class));

        // when, then
        assertThrows(PrevStudyException.class, () -> boardService.getBoard(1L));

    }

    @Test
    @DisplayName("게시글_상세_조회_성공")
    void 게시글_상세_조회_성공() {
        // given
        Board targetBoard = newBoard();
        Long boardId = targetBoard.getId();

        doReturn(Optional.of(targetBoard)).when(boardRepository).findById(boardId);

        // when
        BoardResponse findBoardDto = boardService.getBoard(boardId);

        // then
        assertThat(findBoardDto).isNotNull();
        assertThat(findBoardDto.getBoardId()).isEqualTo(targetBoard.getId());
        assertThat(findBoardDto.getTitle()).isEqualTo(targetBoard.getTitle());
        assertThat(findBoardDto.getContent()).isEqualTo(targetBoard.getContent());

        verify(boardRepository, times(1)).findById(boardId);
    }

    @Test
    @DisplayName("게시글_전체_조회_성공")
    void 게시글_전체_조회_성공() {
        // given
        Board findBoard = newBoard();
        List<Board> findBoards = List.of(findBoard, findBoard);

        doReturn(findBoards).when(boardRepository).findAll();

        // when
        List<BoardResponse> findBoardsDto = boardService.getBoards();

        // then
        assertThat(findBoardsDto).isNotEmpty();
        assertThat(findBoardsDto.size()).isEqualTo(2);

        verify(boardRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("게시글_전체_조회_빈배열")
    void 게시글_전체_조회_빈배열() {
        // given
        doReturn(List.of()).when(boardRepository).findAll();

        // when
        List<BoardResponse> findBoardsDto = boardService.getBoards();

        // then
        assertThat(findBoardsDto).isEmpty();
        verify(boardRepository, times(1)).findAll();
    }



}
