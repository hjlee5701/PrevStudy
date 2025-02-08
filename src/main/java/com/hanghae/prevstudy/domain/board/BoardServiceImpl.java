package com.hanghae.prevstudy.domain.board;

import com.hanghae.prevstudy.global.exception.BoardErrorCode;
import com.hanghae.prevstudy.global.exception.PrevStudyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    @Override
    public BoardResponse add(BoardAddRequest boardAddRequest) {
        Board board = Board.builder()
                .title(boardAddRequest.getTitle())
                .writer(boardAddRequest.getWriter())
                .content(boardAddRequest.getContent())
                .password(boardAddRequest.getPassword())
                .build();
        Board savedBoard = boardRepository.save(board);
        return BoardResponse.builder()
                .boardId(savedBoard.getId())
                .title(savedBoard.getTitle())
                .writer(savedBoard.getWriter())
                .content(savedBoard.getContent())
                .regAt(savedBoard.getRegAt())
                .build();
    }

    @Override
    public BoardResponse getBoard(Long boardId) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new PrevStudyException(BoardErrorCode.FAIL_GET_BOARD));

        return BoardResponse.builder()
                .boardId(findBoard.getId())
                .title(findBoard.getTitle())
                .writer(findBoard.getWriter())
                .content(findBoard.getContent())
                .regAt(findBoard.getRegAt())
                .build();
    }
}
