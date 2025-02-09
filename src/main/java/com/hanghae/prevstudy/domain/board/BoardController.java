package com.hanghae.prevstudy.domain.board;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResponse> addBoard(@Valid @RequestBody BoardAddRequest boardAddRequest) {
        return ResponseEntity.ok(boardService.add(boardAddRequest));

    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable("boardId") Long boardId) {
        return ResponseEntity.ok(boardService.getBoard(boardId));
    }


    @GetMapping
    public ResponseEntity<List<BoardResponse>> getBoards() {
        return ResponseEntity.ok(boardService.getBoards());
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<BoardResponse> updateBoard(
            @PathVariable("boardId") Long boardId,
            @Valid @RequestBody BoardUpdateRequest boardUpdateRequest) {
        return ResponseEntity.ok(boardService.update(boardId, boardUpdateRequest));
    }
}
