package com.hanghae.prevstudy.domain.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class TokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";

    private final String secret; // @Valid 필드 주입 방식

    private final long tokenValidity;


    @PostConstruct
    public void init() {
        System.out.println("JWT Secret: " + secret);
    }

    private Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.tokenValidity}") long tokenValidity
    ) {

        this.secret = secret;
        this.tokenValidity = tokenValidity * 1000;
    }


    // InitializingBean : 빈 초기화 후 properties 세팅한다.
    @Override
    public void afterPropertiesSet() throws Exception {
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