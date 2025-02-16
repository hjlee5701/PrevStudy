package com.hanghae.prevstudy.domain.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static com.hanghae.prevstudy.domain.security.JwtErrorCode.*;

@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";

    private final String secret; // @Valid 필드 주입 방식

    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    private Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.accessTokenExpirationMs}") long accessExpSec,
            @Value("${jwt.refreshTokenExpirationMs}") long refreshExpSec
    ) {

        this.secret = secret;
        this.accessTokenExpirationMs = accessExpSec * 1000;
        this.refreshTokenExpirationMs = refreshExpSec * 1000;
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

        long now = System.currentTimeMillis();

        Date accessExpiration = new Date(now + this.accessTokenExpirationMs);
        Date refreshExpiration = new Date(now + this.accessTokenExpirationMs);

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
        return new TokenDto(memberId, accessToken, refreshToken);
    }

    public Claims parseToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new JwtException("JWT 토큰이 제공되지 않았습니다.");
        }

        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (MalformedJwtException e) {
            throw new JwtValidationException(JWT_MALFORMED);
        } catch (SignatureException e) {
            throw new JwtValidationException(JWT_INVALID_SIGNATURE);
        } catch (ExpiredJwtException e) {
            throw new JwtValidationException(JWT_EXPIRED);
        } catch (UnsupportedJwtException e) {
            throw new JwtValidationException(JWT_UNSUPPORTED); // 암호화 알고리즘이 다름
        }
    }
}