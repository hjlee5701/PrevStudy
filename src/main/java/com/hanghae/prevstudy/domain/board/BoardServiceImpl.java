package com.hanghae.prevstudy.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    @Override
    public Board add(Board board) {
        return boardRepository.save(board);
    }
}
