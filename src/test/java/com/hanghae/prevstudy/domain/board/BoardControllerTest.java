package com.hanghae.prevstudy.domain.board;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.prevstudy.global.exception.BoardErrorCode;
import com.hanghae.prevstudy.global.exception.GlobalExceptionHandler;
import com.hanghae.prevstudy.global.exception.PrevStudyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BoardControllerTest {
    private static final String REQUEST_URL = "/board";

    @Mock
    private BoardServiceImpl boardService;
    @InjectMocks
    private BoardController boardController;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(boardController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("mockMVC_널_체크")
    public void mockMvc_널_체크() throws Exception {
        assertThat(boardController).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    private String createAddRequestJson() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            BoardAddRequest boardAddRequest = new BoardAddRequest(
                    "제목", "작성자", "내용", "비밀번호"
            );
            return objectMapper.writeValueAsString(boardAddRequest);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    @Test
    @DisplayName("게시글_생성")
    void 게시글_생성() throws Exception {
        BoardResponse boardResponse = BoardResponse.builder().build();
        doReturn(boardResponse).when(boardService).add(any(BoardAddRequest.class));
        final ResultActions addResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAddRequestJson())
        );
        addResult.andExpect(status().is2xxSuccessful());
    }

    @Test
    void 게시글_생성_요청값_에러() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        BoardAddRequest boardAddRequest = new BoardAddRequest(
                "", "", "", ""
        );
        String json = objectMapper.writeValueAsString(boardAddRequest);

        // when
        ResultActions addResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json));

        // then
        addResult.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("제목을 입력해 주세요."));
    }

    @Test
    @DisplayName("게시글_상세_조회_실패")
    void 게시글_상세_조회_실패() throws Exception {
        // given
        String boardId = "1";

        doThrow(new PrevStudyException(BoardErrorCode.FAIL_GET_BOARD))
                .when(boardService).getBoard(any(Long.class));

        // when
        ResultActions getBoardResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(REQUEST_URL + "/" + boardId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        getBoardResult
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(BoardErrorCode.FAIL_GET_BOARD.getMessage()))
                .andExpect(jsonPath("$.errCode").value(BoardErrorCode.FAIL_GET_BOARD.getErrCode()));
    }

    @Test
    @DisplayName("게시글_전체_조회_성공")
    void 게시글_전체_조회_성공() throws Exception {
        // given
        List<BoardResponse> boardResponses
                = List.of(new BoardResponse(1L, "", "", "", new Date(), new Date()));

        doReturn(boardResponses).when(boardService).getBoards();

        // when
        ResultActions getBoardsResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        getBoardsResult
                .andExpectAll(status().is2xxSuccessful());
    }
}
