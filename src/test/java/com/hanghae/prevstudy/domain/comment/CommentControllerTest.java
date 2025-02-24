package com.hanghae.prevstudy.domain.comment;

import com.hanghae.prevstudy.domain.comment.dto.CommentRequest;
import com.hanghae.prevstudy.domain.comment.dto.CommentResponse;
import com.hanghae.prevstudy.domain.comment.service.CommentServiceImpl;
import com.hanghae.prevstudy.domain.common.AbstractControllerTest;
import com.hanghae.prevstudy.global.exception.PrevStudyException;
import com.hanghae.prevstudy.global.exception.errorCode.BoardErrorCode;
import com.hanghae.prevstudy.global.exception.errorCode.CommentErrorCode;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest extends AbstractControllerTest {


    @Mock
    CommentServiceImpl commentService;

    @InjectMocks
    CommentController commentController;

    private final String COMMENT_URL = "/comment";

    @Override
    protected Object getController() {
        return commentController;
    }

    @Test
    @DisplayName("mockMVC_널_체크")
    public void mockMvc_널_체크() {
        assertThat(commentController).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    private ResultActions executeAddComment(Long boardId, String content) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders
                        .post(COMMENT_URL + "/" + boardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequestToJson(new CommentRequest(content))));
    }


    @Test
    @DisplayName("댓글_생성_content_blank_에러")
    void 댓글_생성_content_blank_에러() throws Exception {
        // given, when
        ResultActions blankContent = executeAddComment(1L, " ");

        // then
        blankContent.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value("400"),
                jsonPath("$.message").value(Matchers.containsString("content: 내용을 입력해 주세요."))
        );

    }

    @Test
    @DisplayName("댓글_생성_No_board_에러")
    void 댓글_생성_No_board_에러() throws Exception {
        // given
        BDDMockito.given(commentService.add(anyLong(), Mockito.any(), Mockito.any()))
                .willThrow(new PrevStudyException(BoardErrorCode.BOARD_NOT_FOUND));

        // when
        ResultActions noBoardResult = executeAddComment(1L, "내용");

        // then
        noBoardResult.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value("400"),
                jsonPath("$.message").value("게시글이 존재하지 않습니다."),
                jsonPath("$.data").isEmpty()
        );
    }

    @Test
    @DisplayName("댓글_생성_성공")
    void 댓글_생성_성공() throws Exception {
        // given
        BDDMockito.given(commentService.add(anyLong(), Mockito.any(), Mockito.any()))
                .willReturn(new CommentResponse(1L, "test", "내용"));
        // when
        ResultActions resultActions = executeAddComment(1L, "내용");

        resultActions.andExpectAll(
                status().isOk(),
                jsonPath("$.status").value(200),
                jsonPath("$.message").value("댓글 생성 성공"),
                jsonPath("$.data.commentId").isNotEmpty(),
                jsonPath("$.data.writer").isNotEmpty(),
                jsonPath("$.data.content").isNotEmpty()
        );
    }

    private ResultActions executeUpdateComment(String commentId, String content) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders
                        .patch(COMMENT_URL + "/" + commentId)
                        .content(createRequestToJson(new CommentRequest(content)))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    @DisplayName("댓글_수정_실패 - PK 불일치")
    void 댓글_PK_불일치_수정_실패() throws Exception {
        // given
        BDDMockito.given(commentService.update(any(Long.class), any(CommentRequest.class), Mockito.any()))
                .willThrow(new PrevStudyException(CommentErrorCode.COMMENT_NOT_FOUND));

        // when
        ResultActions updateCommentResult = executeUpdateComment("1", "내용2");

        // then
        updateCommentResult.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("댓글이 존재하지 않습니다."),
                jsonPath("$.data").isEmpty()
        );
    }


    @Test
    @DisplayName("댓글_수정_성공")
    void 댓글_수정_성공() throws Exception {
        // given
        BDDMockito.given(commentService.update(any(Long.class), any(CommentRequest.class), Mockito.any()))
                .willReturn(new CommentResponse(1L, "tester", "내용2"));

        // when
        ResultActions updateCommentResult = executeUpdateComment("1", "내용2");

        // then
        updateCommentResult.andExpectAll(
                status().isOk(),
                jsonPath("$.status").value(200),
                jsonPath("$.message").value("댓글 수정 성공"),
                jsonPath("$.data.commentId").isNotEmpty(),
                jsonPath("$.data.writer").isNotEmpty(),
                jsonPath("$.data.content").isNotEmpty()
        );
    }


}
