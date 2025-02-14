package com.hanghae.prevstudy.domain.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.prevstudy.domain.security.TokenDto;
import com.hanghae.prevstudy.global.exception.GlobalExceptionHandler;
import com.hanghae.prevstudy.global.exception.PrevStudyException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

        // given, when
        ResultActions invalidUsernameRequest = executeSignup("", "비밀번호");
        ResultActions invalidPasswordRequest = executeSignup("회원", "");

        // then
        invalidUsernameRequest.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value(Matchers.containsString("username을 입력해주세요.")),
                jsonPath("$.data").isEmpty()
        );
        invalidPasswordRequest.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value(Matchers.containsString("비밀번호를 입력해주세요.")),
                jsonPath("$.data").isEmpty()
        );
    }

    @Test
    @DisplayName("회원_가입_성공")
    void 회원_가입_성공() throws Exception {

        // given, when
        ResultActions signupResult = executeSignup("회원", "비밀번호");

        // then
        signupResult.andExpectAll(
                status().isOk(),
                jsonPath("$.status").value(200),
                jsonPath("$.message").value("회원가입 성공"),
                jsonPath("$.data").isEmpty()
        );
    }

    private ResultActions executeSignup(String username, String password) throws Exception {
        MemberAddRequest memberAddRequest = new MemberAddRequest(username, password);

        return mockMvc.perform(
                MockMvcRequestBuilders
                        .post(REQUEST_URL)
                        .content(createRequestToJson(memberAddRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    @DisplayName("로그인_요청값_에러")
    void 로그인_요청값_에러() throws Exception {

        // given, when
        ResultActions invalidUsernameRequest = executeLogin("", "비밀번호");
        ResultActions invalidPasswordRequest = executeLogin("회원", "");

        // then
        invalidUsernameRequest.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value(Matchers.containsString("username을 입력해주세요.")),
                jsonPath("$.data").isEmpty()
        );
        invalidPasswordRequest.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value(Matchers.containsString("비밀번호를 입력해주세요.")),
                jsonPath("$.data").isEmpty()
        );
    }

    @Test
    @DisplayName("로그인_실패 - username/password 불일치")
    void 로그인_실패_username_password_불일치() throws Exception {

        // given
        BDDMockito.given(memberService.login(any(LoginRequest.class)))
                .willThrow(new PrevStudyException(MemberErrorCode.FAILED_LOGIN));
        // when
        ResultActions notMemberResult = executeLogin("notMember", "비밀번호");

        notMemberResult.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("회원을 찾을 수 없습니다."),
                jsonPath("$.data").isEmpty()
        );

    }

    @Test
    @DisplayName("로그인_성공")
    void 로그인_성공() throws Exception {
        // given
        TokenDto tokenDto = new TokenDto("1", "accessTokenValue", "refreshTokenValue");
        LoginResponse loginResponse = new LoginResponse(1L);

        BDDMockito.given(memberService.login(any(LoginRequest.class)))
                .willReturn(new AuthResultDto(tokenDto, loginResponse));

        // when
        ResultActions loginResult = executeLogin("hello", "hello");

        // then
        loginResult.andExpect(status().isOk());
        loginResult.andExpectAll(
                status().isOk(),
                header().exists("Authorization"),
                jsonPath("$.status").value(200),
                jsonPath("$.message").value("로그인 성공"),
                jsonPath("$.data.memberId").isNotEmpty()
        );

    }




    private ResultActions executeLogin(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, password);

        return mockMvc.perform(
                MockMvcRequestBuilders
                        .get(REQUEST_URL)
                        .content(createRequestToJson(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

}
