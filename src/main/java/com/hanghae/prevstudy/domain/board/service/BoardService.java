package com.hanghae.prevstudy.domain.board.service;

import com.hanghae.prevstudy.domain.board.dto.BoardAddRequest;
import com.hanghae.prevstudy.domain.board.dto.BoardResponse;
import com.hanghae.prevstudy.domain.board.dto.BoardUpdateRequest;
import com.hanghae.prevstudy.domain.security.dto.UserDetailsImpl;

import java.util.List;

public interface BoardService {
    BoardResponse add(BoardAddRequest board, UserDetailsImpl userDetails);

    BoardResponse getBoard(Long boardId, UserDetailsImpl userDetails);

    List<BoardResponse> getBoards();

    BoardResponse update(Long boardId, BoardUpdateRequest boardUpdateRequest, UserDetailsImpl userDetails);

    void delete(Long boardId, UserDetailsImpl userDetails);
}
