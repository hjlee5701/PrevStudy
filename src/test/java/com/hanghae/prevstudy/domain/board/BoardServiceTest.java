package com.hanghae.prevstudy.domain.board;

import com.hanghae.prevstudy.domain.board.dto.BoardAddRequest;
import com.hanghae.prevstudy.domain.board.dto.BoardResponse;
import com.hanghae.prevstudy.domain.board.dto.BoardUpdateRequest;
import com.hanghae.prevstudy.domain.board.entity.Board;
import com.hanghae.prevstudy.domain.board.repository.BoardRepository;
import com.hanghae.prevstudy.domain.board.service.BoardServiceImpl;
import com.hanghae.prevstudy.domain.member.entity.Member;
import com.hanghae.prevstudy.domain.member.repository.MemberRepository;
import com.hanghae.prevstudy.domain.security.dto.UserDetailsImpl;
import com.hanghae.prevstudy.global.exception.PrevStudyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private BoardServiceImpl boardService;

    @Test
    @DisplayName("게시글_생성")
    void 게시글_생성() {
        // given
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("tester")
                .password("비밀번호")
                .build();
        Member referencedMember = Member.builder().id(userDetails.getId()).username(userDetails.getUsername()).build();

        Board newBoard
                = new Board(1L, "제목", referencedMember, "내용", "비밀번호", new Date(), new Date());

        BDDMockito.given(memberRepository.getReferenceById(anyLong())).willReturn(referencedMember);
        BDDMockito.given(boardRepository.save(any(Board.class))).willReturn(newBoard);

        // when
        BoardAddRequest requestBoardDto = new BoardAddRequest("제목", "작성자", "내용", "비밀번호");
        final BoardResponse boardResponseDto = boardService.add(requestBoardDto, userDetails);

        // then
        assertThat(boardResponseDto).isNotNull();
        assertThat(boardResponseDto)
                .extracting("boardId", "title", "writer", "content")
                .containsExactly(newBoard.getId(), newBoard.getTitle(), newBoard.getWriter().getUsername(), newBoard.getContent());
    }

    @Test
    @DisplayName("게시글_상세_조회_실패 - Board 존재 안함")
    void 게시글_상세_조회_실패() {
        // given
        when(boardRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        PrevStudyException exception = assertThrows(PrevStudyException.class,
                () -> boardService.getBoard(999L, UserDetailsImpl.builder().id(1L).build()));

        // then
        assertThat("게시글이 존재하지 않습니다.").isEqualTo(exception.getMessage());
        assertThat(HttpStatus.BAD_REQUEST).isEqualTo(exception.getHttpStatus());
    }

    @Test
    @DisplayName("게시글_작성자_불일치로_상세_조회_실패")
    void 게시글_작성자_불일치로_상세_조회_실패() {
        // given
        Board findBoard
                = new Board(1L, "제목", Member.builder().id(1L).build(), "내용", "비밀번호", new Date(), new Date());

        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(findBoard));

        // when
        PrevStudyException exception = assertThrows(PrevStudyException.class,
                () -> boardService.getBoard(anyLong(), UserDetailsImpl.builder().id(999L).build()));

        // then
        assertThat("게시글에 대한 접근 권한이 없습니다.").isEqualTo(exception.getMessage());
        assertThat(HttpStatus.FORBIDDEN).isEqualTo(exception.getHttpStatus());
    }

    @Test
    @DisplayName("게시글_상세_조회_성공")
    void 게시글_상세_조회_성공() {
        // given
        Date now = new Date();
        Member writer = Member.builder().id(1L).username("test").build();
        Board savedBoard
                = new Board(1L, "제목", writer, "내용", "비밀번호", now, now);

        when(boardRepository.findById(1L)).thenReturn(Optional.of(savedBoard));

        // when
        BoardResponse findBoardDto = boardService.getBoard(1L, UserDetailsImpl.builder().id(1L).build());

        // then
        assertThat(findBoardDto)
                .extracting("boardId", "title", "writer", "content")
                .containsExactly(savedBoard.getId(), savedBoard.getTitle(), savedBoard.getWriter().getUsername(), savedBoard.getContent());

        verify(boardRepository, times(1)).findById(savedBoard.getId());
    }

    @Test
    @DisplayName("게시글_전체_조회_성공")
    void 게시글_전체_조회_성공() {
        // given
        Date now = new Date();
        Board findBoard1
                = new Board(1L, "제목", Member.builder().username("tester1").build(), "내용", "비밀번호", now, now);
        Board findBoard2
                = new Board(2L, "제목", Member.builder().username("tester2").build(), "내용", "비밀번호", now, now);

        List<Board> findBoards = List.of(findBoard1, findBoard2);

        when(boardRepository.findAll()).thenReturn(findBoards);

        // when
        List<BoardResponse> findBoardsDto = boardService.getBoards();

        // then
        assertEquals(2, findBoardsDto.size());
    }

    @Test
    @DisplayName("게시글_전체_조회_빈배열")
    void 게시글_전체_조회_빈배열() {
        // given
        when(boardRepository.findAll()).thenReturn(List.of());

        // when
        List<BoardResponse> findBoardsDto = boardService.getBoards();

        // then
        assertThat(findBoardsDto).isEmpty();
    }

    @Test
    @DisplayName("게시글 수정 실패 - 작성자 불일치")
    void 게시글_작성자_불일치로_수정_실패() {
        // given
        Date now = new Date();
        Board board = new Board(1L, "제목", Member.builder().id(1L).build(), "내용", "비밀번호", now, now);

        BoardUpdateRequest boardUpdateRequest = new BoardUpdateRequest("제목2", "내용2", "비밀번호2");

        BDDMockito.given(boardRepository.findById(anyLong())).willReturn(Optional.of(board));

        // when
        PrevStudyException exception = assertThrows(PrevStudyException.class,
                () -> boardService.update(anyLong(), boardUpdateRequest, UserDetailsImpl.builder().id(999L).build()));

        // then
        assertThat("게시글에 대한 접근 권한이 없습니다.").isEqualTo(exception.getMessage());
        assertThat(HttpStatus.FORBIDDEN).isEqualTo(exception.getHttpStatus());
    }

    @Test
    @DisplayName("게시글 수정 실패 - 비밀번호 불일치")
    void 게시글_비밀번호_불일치() {
        // given
        Date now = new Date();
        Board board = new Board(1L, "제목", Member.builder().id(1L).build(), "내용", "비밀번호", now, now);

        BoardUpdateRequest boardUpdateRequest = new BoardUpdateRequest("제목2", "내용2", "비밀번호2");

        BDDMockito.given(boardRepository.findById(anyLong())).willReturn(Optional.of(board));

        // when
        PrevStudyException exception = assertThrows(PrevStudyException.class,
                () -> boardService.update(1L, boardUpdateRequest, UserDetailsImpl.builder().id(1L).build()));

        // then
        assertThat("비밀번호가 일치하지 않습니다.").isEqualTo(exception.getMessage());
        assertThat(HttpStatus.BAD_REQUEST).isEqualTo(exception.getHttpStatus());
    }


    @Test
    @DisplayName("게시글 수정 성공")
    void 게시글_수정_성공() {
        // given
        Date now = new Date();
        Board beforeUpdateBoard = new Board(1L, "제목", Member.builder().id(1L).build(), "내용", "비밀번호", now, now);

        BoardUpdateRequest boardUpdateRequest = new BoardUpdateRequest("제목2", "내용2", beforeUpdateBoard.getPassword());

        when(boardRepository.findById(1L)).thenReturn(Optional.of(beforeUpdateBoard));

        // when
        BoardResponse boardResponse = boardService.update(1L, boardUpdateRequest, UserDetailsImpl.builder().id(1L).build());

        // then
        assertAll(
                () -> assertThat(boardResponse)
                        .extracting("boardId", "title", "content")
                        .containsExactly(1L, "제목2", "내용2"),

                () -> verify(boardRepository, times(1)).findById(any(Long.class))
        );
    }


    @Test
    @DisplayName("게시글_삭제_실패")
    void 게시글_삭제_실패() {
        // given
        doReturn(Optional.empty()).when(boardRepository).findById(any(Long.class));

        // when
        PrevStudyException exception = assertThrows(PrevStudyException.class,
                () -> boardService.delete(1L, null));

        // then
        assertThat("게시글이 존재하지 않습니다.").isEqualTo(exception.getMessage());
        assertThat(HttpStatus.BAD_REQUEST).isEqualTo(exception.getHttpStatus());

        verify(boardRepository, times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 작성자 불일치")
    void 게시글_작성자_불일치로_삭제_실패() {
        // given
        Date now = new Date();
        Board board = new Board(1L, "제목", Member.builder().id(1L).build(), "내용", "비밀번호", now, now);

        BDDMockito.given(boardRepository.findById(anyLong())).willReturn(Optional.of(board));

        // when
        PrevStudyException exception = assertThrows(PrevStudyException.class,
                () -> boardService.delete(anyLong(), UserDetailsImpl.builder().id(999L).build()));

        // then
        assertThat("게시글에 대한 접근 권한이 없습니다.").isEqualTo(exception.getMessage());
        assertThat(HttpStatus.FORBIDDEN).isEqualTo(exception.getHttpStatus());
    }

    @Test
    @DisplayName("게시글_삭제_성공")
    void 게시글_삭제_성공() {
        // given
        Date now = new Date();
        Board board = new Board(1L, "제목", Member.builder().id(1L).build(), "내용", "비밀번호", now, now);
        Long deleteBoard = board.getId();
        doReturn(Optional.of(board)).when(boardRepository).findById(any(Long.class));

        // when
        boardService.delete(deleteBoard, UserDetailsImpl.builder().id(1L).build());

        // then
        verify(boardRepository, times(1)).delete(board);
    }

}
