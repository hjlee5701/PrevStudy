package com.hanghae.prevstudy.domain.board;

import com.hanghae.prevstudy.global.exception.BoardErrorCode;
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
        Date now = new Date();
        Board newBoard
                = new Board(1L, "제목", "작성자", "내용", "비밀번호", now, now);
        when(boardRepository.save(any(Board.class))).thenReturn(newBoard);

        // when
        BoardAddRequest requestBoardDto = new BoardAddRequest("제목", "작성자", "내용", "비밀번호");
        final BoardResponse boardResponseDto = boardService.add(requestBoardDto);

        // then
        assertThat(boardResponseDto).isNotNull();
        assertThat(boardResponseDto)
                .extracting("boardId", "title", "writer", "content")
                .containsExactly(newBoard.getId(), newBoard.getTitle(), newBoard.getWriter(), newBoard.getContent());
    }

    @Test
    @DisplayName("게시글_상세_조회_실패")
    void 게시글_상세_조회_실패() {
        // given
        when(boardRepository.findById(999L)).thenReturn(Optional.empty());

        // when, then
        PrevStudyException exception = assertThrows(PrevStudyException.class,
                () -> boardService.getBoard(999L));

        assertThat(exception)
                .extracting("errCode")
                .isEqualTo(BoardErrorCode.BOARD_NOT_FOUND.getErrCode());
    }

    @Test
    @DisplayName("게시글_상세_조회_성공")
    void 게시글_상세_조회_성공() {
        // given
        Date now = new Date();
        Board savedBoard
                = new Board(1L, "제목", "작성자", "내용", "비밀번호", now, now);

        when(boardRepository.findById(1L)).thenReturn(Optional.of(savedBoard));

        // when
        BoardResponse findBoardDto = boardService.getBoard(1L);

        // then
        assertThat(findBoardDto)
                .extracting("boardId", "title", "writer", "content")
                .containsExactly(savedBoard.getId(), savedBoard.getTitle(), savedBoard.getWriter(), savedBoard.getContent());

        verify(boardRepository, times(1)).findById(savedBoard.getId());
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

    @Test
    @DisplayName("게시글 수정 실패 - 비밀번호 불일치")
    void 게시글_비밀번호_불일치() {
        // given
        Long boardId = 1L;
        Board board = newBoard(); // 올바른 비밀번호를 가진 게시글

        doReturn(Optional.of(board)).when(boardRepository).findById(boardId);

        BoardUpdateRequest boardUpdateRequest
                = new BoardUpdateRequest("제목2", "내용2", "잘못된 비밀번호");

        // when
        PrevStudyException exception = assertThrows(PrevStudyException.class,
                () -> boardService.update(boardId, boardUpdateRequest));

        // then
        assertThat(exception.getErrCode()).isEqualTo(BoardErrorCode.INVALID_PASSWORD.getErrCode());
        assertThat(exception.getMessage()).isEqualTo(BoardErrorCode.INVALID_PASSWORD.getMessage());

        verify(boardRepository, times(1)).findById(boardId);
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void 게시글_수정_성공() {
        // given
        Long boardId = 1L;
        Board board = newBoard();
        String notUpdateTitle = board.getTitle();
        String notUpdateContent = board.getContent();

        BoardUpdateRequest boardUpdateRequest
                = new BoardUpdateRequest("제목2", "내용2", board.getPassword());

        doReturn(Optional.of(board)).when(boardRepository).findById(boardId);

        // when
        BoardResponse boardResponse = boardService.update(boardId, boardUpdateRequest);

        // then
        assertThat(boardResponse.getBoardId()).isEqualTo(board.getId());
        assertThat(boardResponse.getTitle()).isEqualTo(boardUpdateRequest.getTitle());
        assertThat(boardResponse.getContent()).isEqualTo(boardUpdateRequest.getContent());

        assertThat(boardResponse.getTitle()).isNotEqualTo(notUpdateTitle);
        assertThat(boardResponse.getContent()).isNotEqualTo(notUpdateContent);

        verify(boardRepository, times(1)).findById(boardId);
    }
    
    @Test
    @DisplayName("게시글_삭제_실패")
    void 게시글_삭제_실패() {
        // given
        doReturn(Optional.empty()).when(boardRepository).findById(any(Long.class));

        // when
        PrevStudyException exception = assertThrows(PrevStudyException.class,
                () -> boardService.delete(1L));

        // then
        assertThat(exception.getErrCode()).isEqualTo(BoardErrorCode.BOARD_NOT_FOUND.getErrCode());
        assertThat(exception.getMessage()).isEqualTo(BoardErrorCode.BOARD_NOT_FOUND.getMessage());

        verify(boardRepository, times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("게시글_삭제_성공")
    void 게시글_삭제_성공() {
        // given
        Board board = newBoard();
        Long deleteBoard = board.getId();
        doReturn(Optional.of(board)).when(boardRepository).findById(any(Long.class));

        // when
        boardService.delete(deleteBoard);

        // then
        verify(boardRepository, times(1)).delete(board);
    }

}
