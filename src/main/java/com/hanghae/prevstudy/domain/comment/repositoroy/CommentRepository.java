package com.hanghae.prevstudy.domain.comment.repositoroy;

import com.hanghae.prevstudy.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
