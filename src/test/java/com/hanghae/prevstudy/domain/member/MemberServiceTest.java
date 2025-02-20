package com.hanghae.prevstudy.domain.member;

import com.hanghae.prevstudy.domain.member.dto.AuthResultDto;
import com.hanghae.prevstudy.domain.member.dto.LoginRequest;
import com.hanghae.prevstudy.domain.member.dto.LoginResponse;
import com.hanghae.prevstudy.domain.member.dto.SignupRequest;
import com.hanghae.prevstudy.domain.member.entity.Member;
import com.hanghae.prevstudy.domain.member.exception.MemberErrorCode;
import com.hanghae.prevstudy.domain.member.repository.MemberRepository;
import com.hanghae.prevstudy.domain.member.service.MemberServiceImpl;
import com.hanghae.prevstudy.domain.security.TokenDto;
import com.hanghae.prevstudy.domain.security.TokenProvider;
import com.hanghae.prevstudy.global.exception.PrevStudyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Test
    @DisplayName("회원_가입_username_중복")
    void 회원_가입_username_중복() {
        // given
        SignupRequest signupRequest = new SignupRequest("회원", "비밀번호");
        Member newMember = Member.builder()
                .id(1L)
                .username("회원")
                .password("비밀번호")
                .build();
        when(memberRepository.findByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.of(newMember));

        // when
        PrevStudyException exception =
                assertThrows(PrevStudyException.class, () -> memberService.signup(signupRequest));

        // then
        assertAll(
                () -> assertEquals(MemberErrorCode.DUPLICATE_USERNAME.getHttpStatus(), exception.getHttpStatus()),
                () -> assertEquals("중복된 username 입니다.", exception.getMessage())
        );
    }

    @Test
    @DisplayName("회원_가입_성공")
    void 회원_가입_성공() {
        // given
        SignupRequest signupRequest = new SignupRequest("회원", "비밀번호");
        Member newMember = Member.builder()
                .id(1L)
                .username("회원")
                .password("비밀번호")
                .build();

        when(memberRepository.save(ArgumentMatchers.any(Member.class))).thenReturn(newMember);

        // when, then
        assertDoesNotThrow(() -> memberService.signup(signupRequest));
    }

    @Test
    @DisplayName("로그인_실패 - 비밀번호 불일치")
    void 로그인_실패_비밀번호_불일치() {
        // given
        LoginRequest loginRequest = new LoginRequest("회원", "비밀번호2");
        Member findMember = Member.builder()
                .id(1L)
                .username("회원")
                .password("비밀번호")
                .build();

        BDDMockito.given(memberRepository.findByUsername(anyString()))
                .willReturn(Optional.of(findMember));


        // when
        PrevStudyException memberException = assertThrows(PrevStudyException.class,
                () -> memberService.login(loginRequest)
        );

        // then
        assertAll(
                () -> assertEquals("회원을 찾을 수 없습니다.", memberException.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, memberException.getHttpStatus())
        );

    }


    @Test
    @DisplayName("로그인_성공")
    void 로그인_성공() {
        // given
        Member findMember = Member.builder()
                .id(1L)
                .username("회원")
                .password("비밀번호")
                .build();

        BDDMockito.given(memberRepository.findByUsername(Mockito.any()))
                .willReturn(Optional.of(findMember));

        BDDMockito.given(tokenProvider.createToken(Mockito.anyString()))
                .willReturn(new TokenDto("memberId", "access-token", "refresh-token"));

        // when
        LoginRequest loginRequest = new LoginRequest("회원", "비밀번호");
        AuthResultDto authResultDto = memberService.login(loginRequest);

        LoginResponse loginResponse = authResultDto.getLoginResponse();
        TokenDto tokenDto = authResultDto.getTokenDto();

        // then
        assertNotNull(authResultDto);
        assertNotNull(loginResponse.getMemberId());
        assertFalse(tokenDto.getAccessToken().isEmpty());
        assertFalse(tokenDto.getRefreshToken().isEmpty());
    }



}
