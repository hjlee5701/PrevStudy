package com.hanghae.prevstudy.domain.security;

import com.hanghae.prevstudy.domain.security.dto.UserDetailsImpl;
import com.hanghae.prevstudy.domain.security.service.TokenProvider;
import com.hanghae.prevstudy.domain.security.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationTest {

    private final String AUTHENTICATED_ERROR_URI = "/test/error";
    private final String AUTHENTICATED_PASS_URI = "/test/pass";
    private final String AUTHENTICATED_SUCCESS_URI = "/test/success";
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

    @Test
    @DisplayName("AuthMemberArgumentResolver_익명사용자_예외")
    void AuthMemberArgumentResolver_익명사용자_예외() throws Exception {

        // given, when
        ResultActions authRequest = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(AUTHENTICATED_PASS_URI)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        // then
        authRequest.andExpectAll(
                status().isUnauthorized(),
                jsonPath("$.status").value(401),
                jsonPath("$.message").value("인증되지 않은 사용자입니다."),
                jsonPath("$.data").isEmpty()
        );
    }

    @Test
    @DisplayName("AuthMemberArgumentResolver_성공")
    void AuthMemberArgumentResolver_성공() throws Exception {

        // given
        String testToken = tokenProvider.createToken("1").getAccessToken();
        BDDMockito.given(userDetailsService.loadUserByUsername(anyString()))
                .willReturn(UserDetailsImpl.builder().id(1L).authorities(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))).build());

        // when
        ResultActions authRequest = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(AUTHENTICATED_SUCCESS_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer "+ testToken)
        );

        // then
        authRequest.andExpectAll(
                status().isOk(),
                jsonPath("$.status").value(200),
                jsonPath("$.message").value("테스트 성공")
        );
    }
    


}
