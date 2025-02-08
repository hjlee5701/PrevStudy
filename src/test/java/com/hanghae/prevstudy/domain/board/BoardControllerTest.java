package com.hanghae.prevstudy.domain.board;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BoardControllerTest {
    private static final String REQUEST_URL = "/board";

    @InjectMocks
    private BoardController boardController;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(boardController)
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
        BoardAddResponse boardAddResponse = BoardAddResponse.builder().build();
        doReturn(boardAddResponse).when(boardController).addBoard(any(BoardAddRequest.class));
        final ResultActions addResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(REQUEST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createAddRequestJson())
        );
        addResult.andExpect(status().is2xxSuccessful());
    }

}
