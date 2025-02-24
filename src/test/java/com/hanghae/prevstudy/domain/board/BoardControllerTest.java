package com.hanghae.prevstudy.domain.board;

import com.hanghae.prevstudy.domain.board.controller.BoardController;
import com.hanghae.prevstudy.domain.board.dto.BoardAddRequest;
import com.hanghae.prevstudy.domain.board.dto.BoardResponse;
import com.hanghae.prevstudy.domain.board.dto.BoardUpdateRequest;
import com.hanghae.prevstudy.domain.board.service.BoardServiceImpl;
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

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BoardControllerTest extends AbstractControllerTest {
    private static final String REQUEST_URL = "/board";

    @Mock
    private BoardServiceImpl boardService;
    @InjectMocks
    private BoardController boardController;

    @Override
    protected Object getController() {
        return boardController;
    }

    @Test
    @DisplayName("mockMVC_널_체크")
    public void mockMvc_널_체크() {
        assertThat(boardController).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    private ResultActions executeAddBoard(String title, String writer, String content, String password) throws Exception {
        BoardAddRequest boardAddRequest = new BoardAddRequest(title, writer, content, password);
        return mockMvc.perform(
                MockMvcRequestBuilders
                        .post(REQUEST_URL)
                        .content(createRequestToJson(boardAddRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    @DisplayName("게시글_생성")
    void 게시글_생성() throws Exception {

        // given
        BoardResponse boardResponse = BoardResponse.builder()
                .boardId(1L)
                .title("더미 제목")
                .writer("더미 작성자")
                .content("더미 내용")
                .regAt(new Date())
                .modAt(new Date())
                .build();

        BDDMockito.given(boardService.add(any(BoardAddRequest.class), Mockito.any()))
                .willReturn(boardResponse);

        // when
        ResultActions addResult = executeAddBoard("제목", "작성자", "내용", "비밀번호");

        // then
        addResult.andExpectAll(
                status().isOk(),
                jsonPath("$.status").value(200),
                jsonPath("$.message").value("게시글 생성 성공"),
                jsonPath("$.data.boardId").exists(),
                jsonPath("$.data.title").isString(),
                jsonPath("$.data.content").isString(),
                jsonPath("$.data.writer").isString(),
                jsonPath("$.data.regAt").exists(),
                jsonPath("$.data.modAt").exists()
        );
    }

    @Test
    void 게시글_생성_요청값_에러() throws Exception {
        // given, when
        ResultActions blankTitle = executeAddBoard("", "작성자", "내용", "비밀번호");
        ResultActions blankAll = executeAddBoard(" ", "", "", "");

        // then
        blankTitle.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value("400"),
                jsonPath("$.message").value("[title: 제목을 입력해 주세요.]"),
                jsonPath("$.data").isEmpty()
        );
        blankAll.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value("400"),
                jsonPath("$.message").value(Matchers.containsString("title: 제목을 입력해 주세요.")),
                jsonPath("$.message").value(Matchers.containsString("password: 비밀번호를 입력해 주세요.")),
                jsonPath("$.message").value(Matchers.containsString("writer: 작성자를 입력해 주세요.")),
                jsonPath("$.message").value(Matchers.containsString("content: 내용을 입력해 주세요."))
        );

    }

    @Test
    @DisplayName("게시글_상세_조회_실패")
    void 게시글_상세_조회_실패() throws Exception {
        // given
        String boardId = "1";

        doThrow(new PrevStudyException(BoardErrorCode.BOARD_NOT_FOUND))
                .when(boardService).getBoard(anyLong(), Mockito.any());

        // when
        ResultActions getBoardResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(REQUEST_URL + "/" + boardId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        getBoardResult.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value("400"),
                jsonPath("$.message").value("게시글이 존재하지 않습니다."),
                jsonPath("$.data").isEmpty()
        );
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
        getBoardsResult.andExpectAll(
                status().isOk(),
                jsonPath("$.status").value("200"),
                jsonPath("$.message").value("게시글 전체 조회 성공"),
                jsonPath("$.data").isArray()
        );
    }


    private ResultActions executeUpdateBoard(String boardId, BoardUpdateRequest boardUpdateRequest) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders
                        .patch(REQUEST_URL + "/" + boardId)
                        .content(createRequestToJson(boardUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    @DisplayName("게시글_수정_실패 - 비밀번호 불일치")
    void 게시글_수정_실패() throws Exception {
        // given
        BDDMockito.given(boardService.update(any(Long.class), any(BoardUpdateRequest.class), Mockito.any()))
                .willThrow(new PrevStudyException(BoardErrorCode.INVALID_PASSWORD));

        // when
        ResultActions updateBoardResult = executeUpdateBoard(
                "1",
                new BoardUpdateRequest("제목2", "내용2", "비밀번호2")
        );
        // then
        updateBoardResult.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("비밀번호가 일치하지 않습니다."),
                jsonPath("$.data").isEmpty()
        );
    }


    @Test
    @DisplayName("게시글_수정_성공")
    void 게시글_수정_성공() throws Exception {
        // given
        BoardResponse boardResponse = BoardResponse.builder()
                .boardId(1L)
                .title("더미 제목")
                .writer("더미 작성자")
                .content("더미 내용")
                .regAt(new Date())
                .modAt(new Date())
                .build();

        BDDMockito.given(boardService.update(any(Long.class), any(BoardUpdateRequest.class), Mockito.any()))
                .willReturn(boardResponse);

        // when
        ResultActions updateBoardResult = executeUpdateBoard(
                "1",
                new BoardUpdateRequest("제목 수정", "내용 수정", "비밀번호")
        );

        // then
        updateBoardResult.andExpectAll(
                status().isOk(),
                jsonPath("$.status").value(200),
                jsonPath("$.message").value("게시글 수정 성공"),
                jsonPath("$.data.boardId").exists(),
                jsonPath("$.data.title").isString(),
                jsonPath("$.data.content").isString(),
                jsonPath("$.data.writer").isString(),
                jsonPath("$.data.regAt").exists(),
                jsonPath("$.data.modAt").exists()
        );
    }


    @Test
    @DisplayName("게시글_삭제")
    void 게시글_삭제() throws Exception {
        // given
        Long boardId = 1L;

        // when
        ResultActions deleteResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .delete(REQUEST_URL + "/" + boardId)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        deleteResult.andExpectAll(
                status().isOk(),
                jsonPath("$.status").value(200),
                jsonPath("$.message").value("게시글 삭제 성공"),
                jsonPath("$.data").isEmpty()
        );
    }
    
    
}
