package com.hanghae.prevstudy.domain.comment.service;

import com.hanghae.prevstudy.domain.comment.dto.CommentRequest;
import com.hanghae.prevstudy.domain.comment.dto.CommentResponse;
import com.hanghae.prevstudy.global.resolver.AuthMemberDto;

public interface CommentService {
    CommentResponse add(Long boardId, CommentRequest commentAddRequest, AuthMemberDto authMemberDto);
}
