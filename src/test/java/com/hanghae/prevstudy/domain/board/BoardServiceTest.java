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
        Long invalidBoardId = 999L;
        doReturn(Optional.empty()).when(boardRepository).findById(invalidBoardId);

        // when, then
        PrevStudyException exception = assertThrows(PrevStudyException.class,
                () -> boardService.getBoard(invalidBoardId));

        assertThat(BoardErrorCode.BOARD_NOT_FOUND.getErrCode()).isEqualTo(exception.getErrCode());
        assertThat(BoardErrorCode.BOARD_NOT_FOUND.getMessage()).isEqualTo(exception.getMessage());
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

}
