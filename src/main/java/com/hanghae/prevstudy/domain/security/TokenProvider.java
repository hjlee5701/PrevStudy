package com.hanghae.prevstudy.domain.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";

    private final String secret; // @Valid 필드 주입 방식

    private final long tokenValidity;

    private Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.tokenValidity}") long tokenValidity
    ) {

        this.secret = secret;
        this.tokenValidity = tokenValidity * 1000;
    }


    // InitializingBean 인터페이스 : 빈 초기화 후 afterPropertiesSet 을 구현해야 한다. -> Spring 에 의존적
    // @Value 값의 주입이 보장(=빈 생성) 된 후에 해당 값에 대한 후처리를 해야 NPE 를 방지할 수 있다.
    // 대체 방법 : @PostConstruct -> 일반 자바 환경에서도 동작 가능
    @PostConstruct
    public void init() {
        // secret 문자열을 사용해 토큰 생성에 필요한 key 를 만든다.
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto createToken(String memberId) {

        long now = (new Date()).getTime();
        Date accessExpiration = new Date(now + 99300000); // 5분
        Date refreshExpiration = new Date(now + this.tokenValidity);

        String accessToken = Jwts.builder()
                .claim(AUTHORITIES_KEY, "auth")
                .subject(memberId)
                .expiration(accessExpiration)
                .signWith(key)
                .compact();

        String refreshToken = Jwts.builder()
                .claim(AUTHORITIES_KEY, "auth")
                .subject(memberId)
                .expiration(refreshExpiration)
                .signWith(key)
                .compact();
        return new TokenDto(accessToken, refreshToken);
    }
}