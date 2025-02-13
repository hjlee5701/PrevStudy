package com.hanghae.prevstudy.domain.member;

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
        MemberAddRequest memberAddRequest = new MemberAddRequest("회원", "비밀번호");
        Member newMember = Member.builder()
                .id(1L)
                .username("회원")
                .password("비밀번호")
                .build();
        when(memberRepository.findByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.of(newMember));

        // when
        PrevStudyException exception =
                assertThrows(PrevStudyException.class, () -> memberService.signup(memberAddRequest));

        // then
        assertAll(
                () -> assertEquals(MemberErrorCode.DUPLICATE_USERNAME.getStatus(), exception.getStatus()),
                () -> assertEquals("중복된 username 입니다.", exception.getMessage())
        );
    }

    @Test
    @DisplayName("회원_가입_성공")
    void 회원_가입_성공() {
        // given
        MemberAddRequest memberAddRequest = new MemberAddRequest("회원", "비밀번호");
        Member newMember = Member.builder()
                .id(1L)
                .username("회원")
                .password("비밀번호")
                .build();

        when(memberRepository.save(ArgumentMatchers.any(Member.class))).thenReturn(newMember);

        // when, then
        assertDoesNotThrow(() -> memberService.signup(memberAddRequest));
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
                () -> assertEquals("사용자 로그인에 실패했습니다.", memberException.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, memberException.getStatus())
        );

    }


    @Test
    @DisplayName("로그인 성공")
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
                .willReturn(new TokenDto("memberId", "access-token", "refresh-token")); // 실제 값이 아닌 더미 값 사용

        // when
        LoginRequest loginRequest = new LoginRequest("회원", "비밀번호");
        TokenDto actualToken = memberService.login(loginRequest);

        // then
        assertNotNull(actualToken);
        assertNotNull(actualToken.getMemberId());
        assertFalse(actualToken.getAccessToken().isEmpty());
        assertFalse(actualToken.getRefreshToken().isEmpty());
    }



}
