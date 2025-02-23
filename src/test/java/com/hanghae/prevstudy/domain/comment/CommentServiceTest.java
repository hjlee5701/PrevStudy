package com.hanghae.prevstudy.domain.comment;

import com.hanghae.prevstudy.domain.board.entity.Board;
import com.hanghae.prevstudy.domain.board.repository.BoardRepository;
import com.hanghae.prevstudy.domain.comment.dto.CommentAddRequest;
import com.hanghae.prevstudy.domain.comment.dto.CommentResponse;
import com.hanghae.prevstudy.domain.comment.entity.Comment;
import com.hanghae.prevstudy.domain.comment.repositoroy.CommentRepository;
import com.hanghae.prevstudy.domain.comment.service.CommentServiceImpl;
import com.hanghae.prevstudy.domain.member.entity.Member;
import com.hanghae.prevstudy.domain.member.repository.MemberRepository;
import com.hanghae.prevstudy.global.resolver.AuthMemberDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

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
        CommentAddRequest commentAddRequest = new CommentAddRequest("내용");

        Member referMember = Member.builder().id(1L).build();
        Board referBoard = Board.builder().id(1L).title("제목").build();

        BDDMockito.given(memberRepository.getReferenceById(anyLong()))
                .willReturn(referMember);
        BDDMockito.given(boardRepository.getReferenceById(anyLong()))
                .willReturn(referBoard);

        BDDMockito.given(commentRepository.save(any(Comment.class)))
                .willReturn(newComment(referMember, referBoard));


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
}
