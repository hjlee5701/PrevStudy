package com.hanghae.prevstudy.global.resolver;

import com.hanghae.prevstudy.domain.security.dto.UserDetailsImpl;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Builder
@Getter
public class AuthMemberDto {
    private Long id;
    private String username;
    private boolean isAdmin;

    public static AuthMemberDto from(UserDetailsImpl principal) {
        boolean isAdmin = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // 권한 문자열만 추출
                .anyMatch("ROLE_ADMIN"::equals); // "ROLE_ADMIN"이 포함되어 있는지 확인

        return new AuthMemberDto(
                principal.getId(),
                principal.getUsername(),
                isAdmin
        );
    }

}
