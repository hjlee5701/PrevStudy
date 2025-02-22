package com.hanghae.prevstudy.domain.board.controller;

import com.hanghae.prevstudy.domain.board.dto.BoardAddRequest;
import com.hanghae.prevstudy.domain.board.dto.BoardResponse;
import com.hanghae.prevstudy.domain.board.service.BoardService;
import com.hanghae.prevstudy.domain.board.dto.BoardUpdateRequest;
import com.hanghae.prevstudy.domain.security.UserDetailsImpl;
import com.hanghae.prevstudy.global.AuthMemberInfo;
import com.hanghae.prevstudy.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<ApiResponse<BoardResponse>> addBoard(
            @Valid @RequestBody BoardAddRequest boardAddRequest,
            @AuthMemberInfo UserDetailsImpl userDetails
    ) {

        return ResponseEntity.ok(
                ApiResponse.success("게시글 생성 성공", boardService.add(boardAddRequest, null)));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponse>> getBoard(@PathVariable("boardId") Long boardId) {
        return ResponseEntity.ok(
                ApiResponse.success("게시글 상세 조회 성공", boardService.getBoard(boardId, null)));
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getBoards() {
        return ResponseEntity.ok(
                ApiResponse.success("게시글 전체 조회 성공", boardService.getBoards()));
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponse>> updateBoard(
            @PathVariable("boardId") Long boardId,
            @Valid @RequestBody BoardUpdateRequest boardUpdateRequest) {
        return ResponseEntity.ok(
                ApiResponse.success("게시글 수정 성공", boardService.update(boardId, boardUpdateRequest, null)));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(@PathVariable("boardId") Long boardId) {
        boardService.delete(boardId);
        return ResponseEntity.ok(
                ApiResponse.success("게시글 삭제 성공"));
    }
}
