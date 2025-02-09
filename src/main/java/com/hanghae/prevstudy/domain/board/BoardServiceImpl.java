package com.hanghae.prevstudy.domain.board;

import com.hanghae.prevstudy.global.exception.BoardErrorCode;
import com.hanghae.prevstudy.global.exception.PrevStudyException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
                .modAt(savedBoard.getModAt())
                .build();
    }

    @Override
    public BoardResponse getBoard(Long boardId) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new PrevStudyException(BoardErrorCode.BOARD_NOT_FOUND));

        return BoardResponse.builder()
                .boardId(findBoard.getId())
                .title(findBoard.getTitle())
                .writer(findBoard.getWriter())
                .content(findBoard.getContent())
                .regAt(findBoard.getRegAt())
                .modAt(findBoard.getModAt())
                .build();
    }

    @Override
    public List<BoardResponse> getBoards() {
        List<BoardResponse> boardResponses = new ArrayList<>();
        List<Board> findBoards = boardRepository.findAll();
        for (Board board : findBoards) {
            BoardResponse response = BoardResponse.builder()
                    .boardId(board.getId())
                    .title(board.getTitle())
                    .writer(board.getWriter())
                    .content(board.getContent())
                    .regAt(board.getRegAt())
                    .modAt(board.getModAt())
                    .build();
            boardResponses.add(response);
        }
        return boardResponses;
    }

    @Override
    @Transactional
    public BoardResponse update(Long boardId, BoardUpdateRequest boardUpdateRequest) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new PrevStudyException(BoardErrorCode.BOARD_NOT_FOUND));

        if ( !boardUpdateRequest.getPassword().equals(findBoard.getPassword()) ) {
            throw new PrevStudyException(BoardErrorCode.INVALID_PASSWORD);
        }
        findBoard.update(
                boardUpdateRequest.getTitle(),
                boardUpdateRequest.getContent()
        );
        return BoardResponse.builder()
                .boardId(findBoard.getId())
                .title(findBoard.getTitle())
                .writer(findBoard.getWriter())
                .content(findBoard.getContent())
                .regAt(findBoard.getRegAt())
                .modAt(findBoard.getModAt())
                .build();
    }

    @Override
    public void delete(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new PrevStudyException(BoardErrorCode.BOARD_NOT_FOUND));

        boardRepository.delete(board);
    }
}
