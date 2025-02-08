package com.hanghae.prevstudy.domain.board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class BoardControllerTest {

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
}
