package com.hanghae.prevstudy.domain.board;

public interface BoardService {
    BoardResponse add(BoardAddRequest board);

    BoardResponse getBoard(Long boardId);
}
