package com.hanghae.prevstudy.domain.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.prevstudy.global.exception.GlobalExceptionHandler;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;
    private MockMvc mockMvc;

    private final String REQUEST_URL = "/auth";

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
                .standaloneSetup(memberController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }


    @Test
    @DisplayName("회원_가입_요청값_에러")
    void 회원_가입_요청값_에러() throws Exception {
        MemberAddRequest memberAddRequest
                = new MemberAddRequest("", "");
        ResultActions signupResult = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(REQUEST_URL)
                        .content(createRequestToJson(memberAddRequest))
                        .contentType(MediaType.APPLICATION_JSON)

        );
        signupResult.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.username").value("username을 입력해주세요."),
                jsonPath("$.password").value("비밀번호를 입력해주세요.")
        );
    }
}
