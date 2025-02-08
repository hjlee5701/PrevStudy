package com.hanghae.prevstudy.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    @Override
    public BoardAddResponse add(BoardAddRequest boardAddRequest) {
        Board board = Board.builder()
                .title(boardAddRequest.getTitle())
                .writer(boardAddRequest.getWriter())
                .content(boardAddRequest.getContent())
                .password(boardAddRequest.getPassword())
                .build();
        Board savedBoard = boardRepository.save(board);
        return BoardAddResponse.builder()
                .title(savedBoard.getTitle())
                .writer(savedBoard.getWriter())
                .content(savedBoard.getContent())
                .build();
    }
}
