package com.hanghae.prevstudy.domain.comment.service;

import com.hanghae.prevstudy.domain.board.entity.Board;
import com.hanghae.prevstudy.domain.board.repository.BoardRepository;
import com.hanghae.prevstudy.domain.comment.dto.CommentRequest;
import com.hanghae.prevstudy.domain.comment.dto.CommentResponse;
import com.hanghae.prevstudy.domain.comment.entity.Comment;
import com.hanghae.prevstudy.domain.comment.repositoroy.CommentRepository;
import com.hanghae.prevstudy.domain.member.entity.Member;
import com.hanghae.prevstudy.domain.member.repository.MemberRepository;
import com.hanghae.prevstudy.global.exception.PrevStudyException;
import com.hanghae.prevstudy.global.exception.errorCode.BoardErrorCode;
import com.hanghae.prevstudy.global.exception.errorCode.CommentErrorCode;
import com.hanghae.prevstudy.global.resolver.AuthMemberDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;


    @Override
    @Transactional
    public CommentResponse add(Long boardId, CommentRequest commentAddRequest, AuthMemberDto authMemberDto) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new PrevStudyException(BoardErrorCode.BOARD_NOT_FOUND));

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

    @Override
    public CommentResponse update(Long commentId, CommentRequest commentUpdateRequest, AuthMemberDto authMemberDto) {

        Comment findComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new PrevStudyException(CommentErrorCode.COMMENT_NOT_FOUND));

        // 댓글 수정
        findComment.update(commentUpdateRequest.getContent());
        
        return CommentResponse.builder()
                .commentId(findComment.getId())
                .writer(authMemberDto.getUsername())
                .content(findComment.getContent())
                .build();
    }

    @Override
    public void delete(Long commentId, AuthMemberDto authMemberDto) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new PrevStudyException(CommentErrorCode.COMMENT_NOT_FOUND));

        if (!isWriterMatch(authMemberDto.getId(), comment.getWriter().getId())) {
            throw new PrevStudyException(CommentErrorCode.FORBIDDEN_ACCESS);
        }
    }

    public boolean isWriterMatch(Long loginMemberId, Long writerId) {
        return loginMemberId.equals(writerId);
    }
}
