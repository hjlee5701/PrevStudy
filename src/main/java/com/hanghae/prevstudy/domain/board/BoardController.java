package com.hanghae.prevstudy.domain.board;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResponse> addBoard(@Valid @RequestBody BoardAddRequest boardAddRequest) {
        return ResponseEntity.ok(boardService.add(boardAddRequest));

    }

    @GetMapping
    public ResponseEntity<BoardResponse> getBoard(@Param("boardId") Long boardId) {
        return ResponseEntity.ok(boardService.getBoard(boardId));
    }
}
