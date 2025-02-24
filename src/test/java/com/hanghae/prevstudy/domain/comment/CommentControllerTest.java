package com.hanghae.prevstudy.domain.comment;

import com.hanghae.prevstudy.domain.comment.dto.CommentRequest;
import com.hanghae.prevstudy.domain.comment.service.CommentServiceImpl;
import com.hanghae.prevstudy.domain.common.AbstractControllerTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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



}
