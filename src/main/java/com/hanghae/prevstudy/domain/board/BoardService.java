package com.hanghae.prevstudy.domain.board;

import java.util.List;

public interface BoardService {
    BoardResponse add(BoardAddRequest board);

    BoardResponse getBoard(Long boardId);

    List<BoardResponse> getBoards();

    BoardResponse update(Long boardId, BoardUpdateRequest boardUpdateRequest);

    void delete(Long boardId);
}
