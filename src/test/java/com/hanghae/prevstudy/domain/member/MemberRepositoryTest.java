package com.hanghae.prevstudy.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
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


}
