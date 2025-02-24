package com.hanghae.prevstudy.domain.board.service;

import com.hanghae.prevstudy.domain.board.dto.BoardAddRequest;
import com.hanghae.prevstudy.domain.board.dto.BoardResponse;
import com.hanghae.prevstudy.domain.board.dto.BoardUpdateRequest;
import com.hanghae.prevstudy.domain.board.entity.Board;
import com.hanghae.prevstudy.domain.board.repository.BoardRepository;
import com.hanghae.prevstudy.domain.comment.dto.CommentResponse;
import com.hanghae.prevstudy.domain.comment.entity.Comment;
import com.hanghae.prevstudy.domain.comment.repositoroy.CommentRepository;
import com.hanghae.prevstudy.domain.member.entity.Member;
import com.hanghae.prevstudy.domain.member.repository.MemberRepository;
import com.hanghae.prevstudy.global.exception.PrevStudyException;
import com.hanghae.prevstudy.global.exception.errorCode.BoardErrorCode;
import com.hanghae.prevstudy.global.resolver.AuthMemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public BoardResponse add(BoardAddRequest boardAddRequest, AuthMemberDto authMemberDto) {

        Member member = memberRepository.getReferenceById(authMemberDto.getId()); // 프록시 객체
        Board board = Board.builder()
                .title(boardAddRequest.getTitle())
                .writer(member)
                .content(boardAddRequest.getContent())
                .password(boardAddRequest.getPassword())
                .build();
        Board savedBoard = boardRepository.save(board);
        return BoardResponse.builder()
                .boardId(savedBoard.getId())
                .title(savedBoard.getTitle())
                .writer(authMemberDto.getUsername()) // member 접근 X
                .content(savedBoard.getContent())
                .regAt(savedBoard.getRegAt())
                .modAt(savedBoard.getModAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponse getBoard(Long boardId, AuthMemberDto authMemberDto) {

        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new PrevStudyException(BoardErrorCode.BOARD_NOT_FOUND));

        Long loginMemberId = authMemberDto.getId();
        Member writer = findBoard.getWriter();

        if (!authMemberDto.isAdmin() && !isWriterMatch(loginMemberId, writer.getId())) {
            throw new PrevStudyException(BoardErrorCode.FORBIDDEN_ACCESS);
        }

        return BoardResponse.builder()
                .boardId(findBoard.getId())
                .title(findBoard.getTitle())
                .writer(writer.getUsername())
                .content(findBoard.getContent())
                .comment(convertToCommentResponses(findBoard.getComments()))
                .regAt(findBoard.getRegAt())
                .modAt(findBoard.getModAt())
                .build();
    }

    @Override
    @Transactional(readOnly=true)
    public List<BoardResponse> getBoards() {
        return boardRepository.findAll().stream()
                .map(this::convertToBoardResponse)
                .collect(Collectors.toList());
    }


    private BoardResponse convertToBoardResponse(Board board) {
        return BoardResponse.builder()
                .boardId(board.getId())
                .title(board.getTitle())
                .writer(board.getWriter().getUsername())
                .content(board.getContent())
                .regAt(board.getRegAt())
                .modAt(board.getModAt())
                .comment(convertToCommentResponses(board.getComments())) // List<Comment> → List<CommentResponse>
                .build();
    }

    private List<CommentResponse> convertToCommentResponses(List<Comment> comments) {
        return Optional.ofNullable(comments)
                .orElse(Collections.emptyList())  // comments가 null이면 빈 리스트 반환
                .stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getWriter().getUsername(),
                        comment.getContent()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BoardResponse update(Long boardId, BoardUpdateRequest boardUpdateRequest, AuthMemberDto authMemberDto) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new PrevStudyException(BoardErrorCode.BOARD_NOT_FOUND));

        Member writer = findBoard.getWriter();

        // 일반 유저 & 작성자 불일치
        if (!authMemberDto.isAdmin() && !isWriterMatch(authMemberDto.getId(), writer.getId())) {
            throw new PrevStudyException(BoardErrorCode.FORBIDDEN_ACCESS);
        }

        // 비밀번호 불일치
        String findBoardPassword = findBoard.getPassword();
        if (!isPasswordMatch(findBoardPassword, boardUpdateRequest.getPassword())) {
            throw new PrevStudyException(BoardErrorCode.INVALID_PASSWORD);
        }
        findBoard.update(
                boardUpdateRequest.getTitle(),
                boardUpdateRequest.getContent()
        );
        return BoardResponse.builder()
                .boardId(findBoard.getId())
                .title(findBoard.getTitle())
                .writer(writer.getUsername())
                .content(findBoard.getContent())
                .regAt(findBoard.getRegAt())
                .modAt(findBoard.getModAt())
                .build();
    }

    public boolean isPasswordMatch(String findBoardPassword, String updateRequestPassword) {
        return findBoardPassword.equals(updateRequestPassword);
    }

    public boolean isWriterMatch(Long loginMemberId, Long writerId) {
        return loginMemberId.equals(writerId);
    }

    @Override
    @Transactional
    public void delete(Long boardId, AuthMemberDto authMemberDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new PrevStudyException(BoardErrorCode.BOARD_NOT_FOUND));

        // 일반 유저 & 작성자 불일치
        if (!authMemberDto.isAdmin() && !isWriterMatch(authMemberDto.getId(), board.getWriter().getId())) {
            throw new PrevStudyException(BoardErrorCode.FORBIDDEN_ACCESS);
        }
        boardRepository.delete(board);
    }
}
