package com.hanghae.prevstudy.domain.comment;

import com.hanghae.prevstudy.domain.board.entity.Board;
import com.hanghae.prevstudy.domain.board.repository.BoardRepository;
import com.hanghae.prevstudy.domain.comment.entity.Comment;
import com.hanghae.prevstudy.domain.comment.repositoroy.CommentRepository;
import com.hanghae.prevstudy.domain.member.entity.Member;
import com.hanghae.prevstudy.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    MemberRepository memberRepository;


    @Test
    @DisplayName("CommentRepository_널_체크")
    void CommentRepository_isNull() {
        assertThat(commentRepository).isNotNull();
    }

    private Comment savedComment() {
        Member member = memberRepository.save(Member.builder().username("tester").build());
        Board board = boardRepository.save(Board.builder().title("제목").build());
        return commentRepository.save(
                Comment.builder()
                .writer(member)
                .board(board)
                .content("내용")
                .build()
        );
    }

    @Test
    @DisplayName("댓글_저장_성공")
    void 댓글_저장() {
        // given
        final Comment savedComment = savedComment();

        // when, then
        assertThat(savedComment).isNotNull();
    }


    @Test
    @DisplayName("댓글_PK_ID_조회_실패")
    void 댓글_PK_ID_조회_실패 () {
        // given, when, then
        assertThat(commentRepository.findById(999L)).isEmpty();
    }

    @Test
    @DisplayName("댓글_PK_ID_조회_성공")
    void 댓글_PK_ID_조회_성공 () {
        // given
        final Comment savedComment = savedComment();

        // when, then
        assertThat(commentRepository.findById(savedComment.getId())).isNotEmpty();
    }
    
    
    @Test
    @DisplayName("댓글_수정_성공")
    void 댓글_수정_성공 () {
        // given
        final Comment savedComment = savedComment();

        Optional <Comment> beforeUpdateComment = commentRepository.findById(savedComment.getId());

        assertTrue(beforeUpdateComment.isPresent());
        assertThat(beforeUpdateComment.get().getContent()).isNotEmpty();

        Long prevComId = beforeUpdateComment.get().getId();
        String prevContent = beforeUpdateComment.get().getContent();

        // when
        beforeUpdateComment.get().update("내용 수정");
        commentRepository.flush();
        
        // then
        Optional<Comment> updatedComment = commentRepository.findById(beforeUpdateComment.get().getId());
        assertTrue(updatedComment.isPresent());
        assertThat(updatedComment.get().getContent()).isNotEmpty();
        assertEquals(prevComId, updatedComment.get().getId());
        assertNotEquals(prevContent, updatedComment.get().getContent());
    }

    @Test
    @DisplayName("댓글_삭제_성공")
    void 댓글_삭제_성공 () {
        // given
        final Comment savedComment = savedComment();

        Optional <Comment> prevDeleteComment = commentRepository.findById(savedComment.getId());

        assertTrue(prevDeleteComment.isPresent());
        assertThat(prevDeleteComment.get().getContent()).isNotEmpty();

        // when
        commentRepository.deleteById(prevDeleteComment.get().getId());
        commentRepository.flush();

        // then
        Optional<Comment> updatedComment = commentRepository.findById(prevDeleteComment.get().getId());
        assertTrue(updatedComment.isEmpty());
    }

}
