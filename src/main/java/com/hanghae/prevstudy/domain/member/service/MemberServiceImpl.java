package com.hanghae.prevstudy.domain.member.service;

import com.hanghae.prevstudy.domain.member.entity.Member;
import com.hanghae.prevstudy.domain.member.exception.MemberErrorCode;
import com.hanghae.prevstudy.domain.member.repository.MemberRepository;
import com.hanghae.prevstudy.domain.member.dto.AuthResultDto;
import com.hanghae.prevstudy.domain.member.dto.LoginRequest;
import com.hanghae.prevstudy.domain.member.dto.LoginResponse;
import com.hanghae.prevstudy.domain.member.dto.SignupRequest;
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
    public void signup(SignupRequest signupRequest) {

        if (isDuplicateUsername(signupRequest.getUsername())) {
            throw new PrevStudyException(MemberErrorCode.DUPLICATE_USERNAME);
        }

        Member newMember = Member.builder()
                .username(signupRequest.getUsername())
                .password(signupRequest.getPassword())
                .build();

        memberRepository.save(newMember);
    }

    private boolean isDuplicateUsername(String username) {
        return memberRepository.findByUsername(username).isPresent();
    }

    @Override
    public AuthResultDto login(LoginRequest loginRequest) {

        Member member = memberRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new PrevStudyException(MemberErrorCode.FAILED_LOGIN));

        String storedPassword = member.getPassword();
        if (!isValidPassword(storedPassword, loginRequest.getPassword())) {
            throw new PrevStudyException(MemberErrorCode.FAILED_LOGIN);
        }
        TokenDto tokenDto = tokenProvider.createToken(member.getId().toString());

        return new AuthResultDto(tokenDto, new LoginResponse(member.getId()));
    }

    private boolean isValidPassword(String storedPassword, String inputPassword) {
        return inputPassword.equals(storedPassword);
    }
}
