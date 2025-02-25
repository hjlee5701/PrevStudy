package com.hanghae.prevstudy.domain.board.service;

import com.hanghae.prevstudy.domain.board.dto.BoardAddRequest;
import com.hanghae.prevstudy.domain.board.dto.BoardResponse;
import com.hanghae.prevstudy.domain.board.dto.BoardUpdateRequest;
import com.hanghae.prevstudy.global.resolver.AuthMemberDto;

import java.util.List;

public interface BoardService {
    BoardResponse add(BoardAddRequest board, AuthMemberDto authMemberDto);

    BoardResponse getBoard(Long boardId, AuthMemberDto authMemberDto);

    List<BoardResponse> getBoards();

    BoardResponse update(Long boardId, BoardUpdateRequest boardUpdateRequest, AuthMemberDto authMemberDto);

    void delete(Long boardId, AuthMemberDto authMemberDto);
}
