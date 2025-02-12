package com.hanghae.prevstudy.domain.member;

import com.hanghae.prevstudy.global.exception.PrevStudyException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void signup(MemberAddRequest memberAddRequest) {

        if (isDuplicateUsername(memberAddRequest.getUsername())) {
            throw new PrevStudyException(MemberErrorCode.DUPLICATE_USERNAME);
        }

        Member newMember = Member.builder()
                .username(memberAddRequest.getUsername())
                .password(memberAddRequest.getPassword())
                .build();

        memberRepository.save(newMember);
    }

    private boolean isDuplicateUsername(String username) {
        return memberRepository.findByUsername(username).isPresent();
    }

    @Override
    public void login(LoginRequest loginRequest) {
        Member member = memberRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow();
        throw new PrevStudyException(MemberErrorCode.FAILED_LOGIN);
    }
}
