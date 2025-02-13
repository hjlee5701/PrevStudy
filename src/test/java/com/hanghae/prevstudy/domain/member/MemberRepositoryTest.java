package com.hanghae.prevstudy.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest
@ActiveProfiles("test")
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("MemberRepository_널_체크")
    void MemberRepository_isNull() {
        assertThat(memberRepository).isNotNull();
    }

    @Test
    @DisplayName("회원_가입_username_중복")
    void 회원_가입_username_중복() {
        // given
        Member member1 = Member.builder()
                .username("회원")
                .password("비밀번호")
                .build();
        memberRepository.save(member1);
        memberRepository.flush();

        // when
        Member member2 = Member.builder()
                .username("회원")
                .password("비밀번호")
                .build();

        memberRepository.save(member2);

        // then
        assertThrows(DataAccessException.class, () -> memberRepository.flush());
    }

    @Test
    @DisplayName("회원_가입_성공")
    void 회원_가입_성공() {
        // given
        Member member = Member.builder()
                .username("회원")
                .password("비밀번호")
                .build();

        // when
        Member signUpMember = memberRepository.save(member);

        // then
        assertDoesNotThrow(() -> memberRepository.flush());
        assertThat(memberRepository.findById(signUpMember.getId())).isNotEmpty();
    }

    @Test
    @DisplayName("username_중복여부_조회")
    void username_중복여부_조회() {
        // given
        Member signUpMember = Member.builder()
                .username("가입 회원")
                .password("비밀번호")
                .build();

        memberRepository.save(signUpMember);
        memberRepository.flush();

        // when
        assertThat(memberRepository.findByUsername("가입 회원")).isNotEmpty();
        assertThat(memberRepository.findByUsername("미가입 회원")).isEmpty();
    }

}
