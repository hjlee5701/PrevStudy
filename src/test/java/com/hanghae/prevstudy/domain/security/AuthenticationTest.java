package com.hanghae.prevstudy.domain.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationTest {

    private final String AUTHENTICATED_ERROR_URI = "/test/error";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TokenProvider tokenProvider;
    @MockitoBean // SpringContext 에서 관리하는 가짜 Bean
    UserDetailsServiceImpl userDetailsService;


    @Test
    @DisplayName("토큰_없는_요청")
    void 토큰_없는_요청() throws Exception {
        ResultActions unAuthRequest = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(AUTHENTICATED_ERROR_URI)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        unAuthRequest.andExpectAll(
                status().isUnauthorized(),
                jsonPath("$.status").value(401),
                jsonPath("$.message").value("JWT 토큰이 제공되지 않았습니다."),
                jsonPath("$.data").isEmpty()
        );
    }

    @Test
    @DisplayName("유효하지_않은_토큰_요청")
    void 유효하지_않은_토큰_요청() throws Exception {
        ResultActions unAuthRequest = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(AUTHENTICATED_ERROR_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer invalid-token")
        );
        unAuthRequest.andExpectAll(
                status().isBadRequest(),
                jsonPath("$.status").value(400),
                jsonPath("$.message").value("올바르지 않은 JWT 형식입니다."),
                jsonPath("$.data").isEmpty()
        );
    }


    @Test
    @DisplayName("다른_회원_토큰_요청")
    void 다른_회원_토큰_요청() throws Exception {
        String testToken = tokenProvider.createToken("0").getAccessToken();
        BDDMockito.given(userDetailsService.loadUserByUsername(anyString()))
                .willThrow(new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        ResultActions unAuthRequest = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(AUTHENTICATED_ERROR_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+ testToken)
        );
        unAuthRequest.andExpectAll(
                status().isUnauthorized(),
                jsonPath("$.status").value(401),
                jsonPath("$.message").value("사용자를 찾을 수 없습니다."),
                jsonPath("$.data").isEmpty()
        );
    }

    @Test
    @DisplayName("AuthMemberArgumentResolver_클래스타입_예외")
    void AuthMemberArgumentResolver_클래스타입_예외() throws Exception {
        // given
        String testToken = tokenProvider.createToken("0").getAccessToken();
        BDDMockito.given(userDetailsService.loadUserByUsername(anyString()))
                .willReturn(UserDetailsImpl.builder().build());

        // when
        ResultActions authRequest = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(AUTHENTICATED_ERROR_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+ testToken)
        );

        // then
        authRequest.andExpectAll(
                status().isInternalServerError(),
                jsonPath("$.status").value(500),
                jsonPath("$.message").value("서버 설정 오류입니다. 관리자에게 문의하세요."),
                jsonPath("$.data").isEmpty()
        );
    }


    


}
