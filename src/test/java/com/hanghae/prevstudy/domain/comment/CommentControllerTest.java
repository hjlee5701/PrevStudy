package com.hanghae.prevstudy.domain.comment;

import com.hanghae.prevstudy.domain.comment.dto.CommentRequest;
import com.hanghae.prevstudy.domain.comment.service.CommentServiceImpl;
import com.hanghae.prevstudy.domain.common.AbstractControllerTest;
import com.hanghae.prevstudy.global.exception.PrevStudyException;
import com.hanghae.prevstudy.global.exception.errorCode.BoardErrorCode;
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


}
