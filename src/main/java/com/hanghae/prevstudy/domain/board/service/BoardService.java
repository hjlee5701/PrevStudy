package com.hanghae.prevstudy.domain.board.service;

import com.hanghae.prevstudy.domain.board.dto.BoardAddRequest;
import com.hanghae.prevstudy.domain.board.dto.BoardResponse;
import com.hanghae.prevstudy.domain.board.dto.BoardUpdateRequest;

import java.util.List;

public interface BoardService {
    BoardResponse add(BoardAddRequest board);

    BoardResponse getBoard(Long boardId);

    List<BoardResponse> getBoards();

    BoardResponse update(Long boardId, BoardUpdateRequest boardUpdateRequest);

    void delete(Long boardId);
}
