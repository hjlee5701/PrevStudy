package com.hanghae.prevstudy.domain.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    @DisplayName("게시글_상세_조회")
    void 게시글_상세_조회_실패() {
        // given
        Long notExistBoardId = 1L;
        doReturn(Optional.of(newBoard())).when(boardRepository).findById(any(Long.class));

        // when, then
        assertThrows(PrevStudyException.class, () -> boardService.getBoard(notExistBoardId));
    }
}
