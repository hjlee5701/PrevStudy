package com.hanghae.prevstudy.domain.member;

import com.hanghae.prevstudy.global.exception.PrevStudyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Test
    @DisplayName("회원_가입_username_중복")
    void 회원_가입_username_중복() {
        // given
        MemberAddRequest memberAddRequest = new MemberAddRequest("회원", "비밀번호");

        // when
        PrevStudyException exception =
                assertThrows(PrevStudyException.class, () -> memberService.signup(memberAddRequest));

        // then
        assertAll(
                () -> assertEquals(exception.getStatus(), MemberErrorCode.DUPLICATE_USERNAME.getStatus()),
                () -> assertEquals(exception.getMessage(), "중복된 username 입니다.")
        );
    }



}
