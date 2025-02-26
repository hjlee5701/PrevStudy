package com.hanghae.prevstudy.domain.comment;

import com.hanghae.prevstudy.domain.board.entity.Board;
import com.hanghae.prevstudy.domain.board.repository.BoardRepository;
import com.hanghae.prevstudy.domain.comment.dto.CommentRequest;
import com.hanghae.prevstudy.domain.comment.dto.CommentResponse;
import com.hanghae.prevstudy.domain.comment.entity.Comment;
import com.hanghae.prevstudy.domain.comment.repositoroy.CommentRepository;
import com.hanghae.prevstudy.domain.comment.service.CommentServiceImpl;
import com.hanghae.prevstudy.domain.member.entity.Member;
import com.hanghae.prevstudy.domain.member.repository.MemberRepository;
import com.hanghae.prevstudy.global.exception.PrevStudyException;
import com.hanghae.prevstudy.global.resolver.AuthMemberDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    private final Long ANOTHER_MEMBER_ID = 999L;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment newComment(Member member, Board board) {
        return Comment.builder()
                .id(1L)
                .writer(member)
                .board(board)
                .content("내용")
                .build();
    }

    private AuthMemberDto createAuthMemberDto() {
        return AuthMemberDto.builder().id(1L).username("tester").isAdmin(false).build();
    }

    @Test
    @DisplayName("댓글_생성_성공")
    void 댓글_생성_성공() {
        // given
        CommentRequest commentAddRequest = new CommentRequest("내용");

        Member commentWriter = Member.builder().id(1L).build();
        Board board = Board.builder().id(1L).title("제목").build();

        BDDMockito.given(memberRepository.getReferenceById(anyLong()))
                .willReturn(commentWriter);
        BDDMockito.given(boardRepository.findById(anyLong()))
                .willReturn(Optional.of(board));

        BDDMockito.given(commentRepository.save(any(Comment.class)))
                .willReturn(newComment(commentWriter, board));


        // when
        CommentResponse commentResponse
                = commentService.add(1L, commentAddRequest, createAuthMemberDto());

        // then
        assertAll(
                () -> assertNotNull(commentResponse.getCommentId()),
                () -> assertNotNull(commentResponse.getWriter()),
                () -> assertNotNull(commentResponse.getContent())
        );
    }


    @Test
    @DisplayName("댓글_생성_실패 - 존재하지 않은 게시글")
    void 댓글_생성_실패() {
        // given
        CommentRequest commentAddRequest = new CommentRequest("내용");

        BDDMockito.given(boardRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        PrevStudyException exception = assertThrows(
                PrevStudyException.class,
                () -> commentService.add(anyLong(), commentAddRequest, createAuthMemberDto())
        );

        // then
        assertAll(
                () -> assertEquals("게시글이 존재하지 않습니다.", exception.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus())
        );
    }

    @Test
    @DisplayName("댓글_수정_실패 - 존재하지 않은 댓글")
    void 댓글_수정_실패() {
        // given
        CommentRequest commentUpdateRequest = new CommentRequest("수정 내용");

        BDDMockito.given(commentRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        PrevStudyException exception = assertThrows(
                PrevStudyException.class,
                () -> commentService.update(anyLong(), commentUpdateRequest, createAuthMemberDto())
        );

        // then
        assertAll(
                () -> assertEquals("댓글이 존재하지 않습니다.", exception.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus())
        );
    }

    @Test
    @DisplayName("댓글_수정_실패 - 작성자 불일치")
    void 작성자_불일치_댓글_수정_실패() {
        // given
        CommentRequest commentUpdateRequest = new CommentRequest("수정 내용");

        Member commentWriter = Member.builder().id(ANOTHER_MEMBER_ID).build();
        Board board = Board.builder().id(1L).title("제목").build();

        BDDMockito.given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(newComment(commentWriter, board)));

        // when
        PrevStudyException exception = assertThrows(
                PrevStudyException.class,
                () -> commentService.update(anyLong(), commentUpdateRequest, createAuthMemberDto())
        );

        // then
        assertAll(
                () -> assertEquals("댓글에 대한 접근 권한이 없습니다.", exception.getMessage()),
                () -> assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus())
        );
    }


    @Test
    @DisplayName("댓글_수정_성공")
    void 댓글_수정_성공() {
        // given
        CommentRequest commentUpdateRequest = new CommentRequest("수정 내용");

        Member commentWriter = Member.builder().id(1L).build();
        Board board = Board.builder().id(1L).title("제목").build();
        Comment savedComment = newComment(commentWriter, board);

        String beforeUpdateContent = savedComment.getContent();

        BDDMockito.given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(savedComment));

        // when
        CommentResponse commentResponse
                = commentService.update(anyLong(), commentUpdateRequest, createAuthMemberDto());

        // then
        assertAll(
                () -> assertNotNull(commentResponse.getCommentId()),
                () -> assertNotNull(commentResponse.getWriter()),
                () -> assertNotNull(commentResponse.getContent()),
                () -> assertNotEquals(beforeUpdateContent, commentResponse.getContent())
        );
    }

    @Test
    @DisplayName("댓글_삭제_실패 - 존재하지 않은 댓글")
    void 존재하지_않은_댓글_삭제_실패() {
        // given
        BDDMockito.given(commentRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        PrevStudyException exception = assertThrows(
                PrevStudyException.class,
                () -> commentService.delete(anyLong(), createAuthMemberDto())
        );

        // then
        assertAll(
                () -> assertEquals("댓글이 존재하지 않습니다.", exception.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus())
        );
    }

    @Test
    @DisplayName("댓글_삭제_실패 - 작성자 불일치")
    void 작성자_불일치_댓글_삭제_실패() {
        // given
        Member commentWriter = Member.builder().id(ANOTHER_MEMBER_ID).build();
        Board board = Board.builder().id(1L).title("제목").build();

        BDDMockito.given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(newComment(commentWriter, board)));

        // when
        PrevStudyException exception = assertThrows(
                PrevStudyException.class,
                () -> commentService.delete(anyLong(), createAuthMemberDto())
        );

        // then
        assertAll(
                () -> assertEquals("댓글에 대한 접근 권한이 없습니다.", exception.getMessage()),
                () -> assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus())
        );
    }

    @Test
    @DisplayName("댓글_삭제_성공")
    void 댓글_삭제_성공() {
        // given
        Member commentWriter = Member.builder().id(1L).build();
        Board board = Board.builder().id(1L).title("제목").build();

        BDDMockito.given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(newComment(commentWriter, board)));

        // when, then
        assertDoesNotThrow(() -> commentService.delete(anyLong(), createAuthMemberDto()));
        verify(commentRepository, times(1)).deleteById(anyLong());
    }

    // 관리자
    private AuthMemberDto createAuthAdminDto() {
        return AuthMemberDto.builder().id(777L).username("tester").isAdmin(true).build();
    }

    @Test
    @DisplayName("관리자의_댓글_수정_성공")
    void 관리자의_댓글_수정_성공() {
        // given
        CommentRequest commentUpdateRequest = new CommentRequest("수정 내용");

        Member commentWriter = Member.builder().id(ANOTHER_MEMBER_ID).build();
        Board board = Board.builder().id(1L).title("제목").build();
        Comment savedComment = newComment(commentWriter, board);

        String beforeUpdateContent = savedComment.getContent();

        BDDMockito.given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(savedComment));

        // when
        CommentResponse commentResponse
                = commentService.update(anyLong(), commentUpdateRequest, createAuthAdminDto());

        // then
        assertAll(
                () -> assertNotNull(commentResponse.getCommentId()),
                () -> assertNotNull(commentResponse.getWriter()),
                () -> assertNotNull(commentResponse.getContent()),
                () -> assertNotEquals(beforeUpdateContent, commentResponse.getContent())
        );
    }

    @Test
    @DisplayName("관리자의_댓글_삭제_성공")
    void 관리자의_댓글_삭제_성공() {
        // given
        Member commentWriter = Member.builder().id(ANOTHER_MEMBER_ID).build();
        Board board = Board.builder().id(1L).title("제목").build();

        BDDMockito.given(commentRepository.findById(anyLong()))
                .willReturn(Optional.of(newComment(commentWriter, board)));

        // when, then
        assertDoesNotThrow(() -> commentService.delete(anyLong(), createAuthAdminDto()));
        verify(commentRepository, times(1)).deleteById(anyLong());
    }
}
