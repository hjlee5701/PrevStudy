package com.hanghae.prevstudy.domain.board;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.prevstudy.domain.board.controller.BoardController;
import com.hanghae.prevstudy.domain.board.dto.BoardAddRequest;
import com.hanghae.prevstudy.domain.board.dto.BoardResponse;
import com.hanghae.prevstudy.domain.board.dto.BoardUpdateRequest;
import com.hanghae.prevstudy.domain.board.service.BoardServiceImpl;
import com.hanghae.prevstudy.domain.board.exception.BoardErrorCode;
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
import static org.mockito.Mockito.*;
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

    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private <T> String createRequestToJson(T boardRequest) {
        try {
            return objectMapper.writeValueAsString(boardRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }
    }

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


    @Test
    @DisplayName("게시글_생성")
    void 게시글_생성() throws Exception {
        BoardResponse boardResponse = BoardResponse.builder().build();
        doReturn(boardResponse).when(boardService).add(any(BoardAddRequest.class));
        final ResultActions addResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequestToJson(new BoardAddRequest(
                                "제목", "작성자", "내용", "비밀번호"
                        )))
        );
        addResult.andExpect(status().isOk());
    }

    @Test
    void 게시글_생성_요청값_에러() throws Exception {
        // given
        String json = createRequestToJson(
                new BoardAddRequest("", "", "", "")
        );

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

        doThrow(new PrevStudyException(BoardErrorCode.BOARD_NOT_FOUND))
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
                .andExpect(jsonPath("$.message").value(BoardErrorCode.BOARD_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.errCode").value(BoardErrorCode.BOARD_NOT_FOUND.getErrCode()));
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
                .andExpectAll(status().isOk());
    }


    @Test
    @DisplayName("게시글_수정_실패 - 비밀번호 불일치")
    void 게시글_수정_실패() throws Exception {
        // given
        Long boardId = 1L;
        BoardUpdateRequest boardUpdateRequest
                = new BoardUpdateRequest("제목2", "내용2", "비밀번호2");

        doThrow(new PrevStudyException(BoardErrorCode.INVALID_PASSWORD))
                .when(boardService).update(any(Long.class), any(BoardUpdateRequest.class));


        // when
        ResultActions updateBoardResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .patch(REQUEST_URL + "/" + boardId)
                        .content(createRequestToJson(boardUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        updateBoardResult
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(BoardErrorCode.INVALID_PASSWORD.getMessage()))
                .andExpect(jsonPath("$.errCode").value(BoardErrorCode.INVALID_PASSWORD.getErrCode()));
    }

    @Test
    @DisplayName("게시글_수정_성공")
    void 게시글_수정_성공() throws Exception {
        // given
        Long boardId = 1L;
        BoardUpdateRequest boardUpdateRequest
                = new BoardUpdateRequest("제목2", "내용2", "비밀번호");

        BoardResponse boardResponse = BoardResponse.builder()
                .boardId(boardId)
                .title(boardUpdateRequest.getTitle())
                .writer("작성자")
                .content(boardUpdateRequest.getContent())
                .regAt(new Date())
                .modAt(new Date())
                .build();

        doReturn(boardResponse).when(boardService).update(any(Long.class), any(BoardUpdateRequest.class));


        // when
        ResultActions updateBoardResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .patch(REQUEST_URL + "/" + boardId)
                        .content(createRequestToJson(boardUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        updateBoardResult
                .andExpectAll(status().isOk());
    }

    @Test
    @DisplayName("게시글_삭제")
    void 게시글_삭제() throws Exception {
        // given
        Long boardId = 1L;

        // when
        ResultActions getBoardsResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .delete(REQUEST_URL + "/" + boardId)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        getBoardsResult
                .andExpectAll(status().isOk());
    }
    
    
}
