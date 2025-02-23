package com.hanghae.prevstudy.domain.comment.service;

import com.hanghae.prevstudy.domain.board.entity.Board;
import com.hanghae.prevstudy.domain.board.repository.BoardRepository;
import com.hanghae.prevstudy.domain.comment.dto.CommentAddRequest;
import com.hanghae.prevstudy.domain.comment.dto.CommentResponse;
import com.hanghae.prevstudy.domain.comment.entity.Comment;
import com.hanghae.prevstudy.domain.comment.repositoroy.CommentRepository;
import com.hanghae.prevstudy.domain.member.entity.Member;
import com.hanghae.prevstudy.domain.member.repository.MemberRepository;
import com.hanghae.prevstudy.global.resolver.AuthMemberDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;


    @Override
    @Transactional
    public CommentResponse add(Long boardId, CommentAddRequest commentAddRequest, AuthMemberDto authMemberDto) {

        Board board = boardRepository.getReferenceById(boardId);
        Member writer = memberRepository.getReferenceById(authMemberDto.getId());

        Comment newComment = Comment.builder()
                .content(commentAddRequest.getContent())
                .writer(writer)
                .board(board)
                .build();
        Comment savedComment = commentRepository.save(newComment);

        return CommentResponse.builder()
                .commentId(savedComment.getId())
                .writer(authMemberDto.getUsername())
                .content(savedComment.getContent())
                .build();
    }
}
