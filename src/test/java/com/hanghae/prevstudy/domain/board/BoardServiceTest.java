package com.hanghae.prevstudy.domain.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

        doReturn(newBoard()).when(boardRepository).save(any(Board.class));

        // when
        Board requestBoard = newBoard();
        final Board savedBoard = boardService.add(requestBoard);

        // then
        assertThat(savedBoard).isNotNull();
        assertThat(savedBoard.getId()).isEqualTo(1L);
        assertThat(savedBoard.getTitle()).isEqualTo("제목");
        assertThat(savedBoard.getContent()).isEqualTo("내용");
        verify(boardRepository, times(1)).save(any(Board.class));
    }
}
