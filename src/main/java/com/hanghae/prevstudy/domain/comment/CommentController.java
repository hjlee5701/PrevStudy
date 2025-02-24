package com.hanghae.prevstudy.domain.comment;

import com.hanghae.prevstudy.domain.comment.dto.CommentRequest;
import com.hanghae.prevstudy.domain.comment.dto.CommentResponse;
import com.hanghae.prevstudy.domain.comment.service.CommentService;
import com.hanghae.prevstudy.global.annotation.AuthMemberInfo;
import com.hanghae.prevstudy.global.resolver.AuthMemberDto;
import com.hanghae.prevstudy.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{boardId}")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment (
            @PathVariable(value = "boardId") long boardId,
            @RequestBody @Valid CommentRequest commentRequest,
            @AuthMemberInfo AuthMemberDto authMemberDto
            ) {

        return ResponseEntity.ok(
                ApiResponse.success("댓글 생성 성공", commentService.add(boardId, commentRequest, authMemberDto)));
    }
}
