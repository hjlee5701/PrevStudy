package com.hanghae.prevstudy.domain.member;

import com.hanghae.prevstudy.domain.security.TokenDto;
import com.hanghae.prevstudy.domain.security.TokenProvider;
import com.hanghae.prevstudy.global.exception.PrevStudyException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

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
    public TokenDto login(LoginRequest loginRequest) {

        Member member = memberRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new PrevStudyException(MemberErrorCode.FAILED_LOGIN));

        String storedPassword = member.getPassword();
        if (!isValidPassword(storedPassword, loginRequest.getPassword())) {
            throw new PrevStudyException(MemberErrorCode.FAILED_LOGIN);
        }
        return tokenProvider.createToken(member.getId().toString());
    }

    private boolean isValidPassword(String storedPassword, String inputPassword) {
        return inputPassword.equals(storedPassword);
    }
}
