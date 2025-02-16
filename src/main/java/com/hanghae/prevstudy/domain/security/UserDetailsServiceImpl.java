package com.hanghae.prevstudy.domain.security;

import com.hanghae.prevstudy.domain.member.entity.Member;
import com.hanghae.prevstudy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 정보 로드 실패"));

        return UserDetailsImpl.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .build();
    }
}
