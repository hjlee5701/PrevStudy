package com.hanghae.prevstudy.domain.security;

import com.hanghae.prevstudy.domain.member.entity.Member;
import com.hanghae.prevstudy.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("토큰_인증_성공_후_사용자_정보_로드_실패")
    void 사용자_정보_로드_실패() {
        // given
        BDDMockito.given(memberRepository.findByUsername(anyString()))
                .willReturn(Optional.empty());
        // when, then
        assertThrows(UsernameNotFoundException.class, ()-> userDetailsService.loadUserByUsername("미인증 사용자"));
    }

    @Test
    @DisplayName("토큰_인증_성공_후_사용자_정보_로드_성공")
    void 사용자_정보_로드_성공() {
        // given
        Member member = new Member(1L, "인증 사용자", "비밀번호");
        
        BDDMockito.given(memberRepository.findByUsername(anyString()))
                .willReturn(Optional.of(member));
        // when
        UserDetails userDetails = userDetailsService.loadUserByUsername("인증 사용자");

        // then
        assertAll(
                () -> assertThat(userDetails.getUsername()).isNotEmpty(),
                () -> assertThat(userDetails.getPassword()).isNotEmpty()
        );
    }
}
