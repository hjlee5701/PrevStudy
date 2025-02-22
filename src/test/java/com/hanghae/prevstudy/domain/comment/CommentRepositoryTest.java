package com.hanghae.prevstudy.domain.comment;

import com.hanghae.prevstudy.domain.comment.entity.Comment;
import com.hanghae.prevstudy.domain.comment.repositoroy.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    CommentRepository commentRepository;

    @Test
    @DisplayName("CommentRepository_널_체크")
    void CommentRepository_isNull() {
        assertThat(commentRepository).isNotNull();
    }

    private Comment newBoard() {
        Comment member = commentRepository.save(Comment.builder().build());
        return Comment.builder()
                .title("제목")
                .writer(member)
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

}
