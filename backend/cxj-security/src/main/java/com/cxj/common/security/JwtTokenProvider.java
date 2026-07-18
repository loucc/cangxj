package com.cxj.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * JWT 令牌工具：签发、解析、校验
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final Duration expiration;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.expiration:7200}") long expirationSeconds,
            @Value("${app.security.jwt.issuer:cxj}") String issuer) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = Duration.ofSeconds(expirationSeconds);
        this.issuer = issuer;
    }

    public String generate(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(subject)
                .issuer(issuer)
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(key)
                .compact();
    }

    public Optional<Claims> parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(claims);
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("JWT parse failed: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    public boolean isValid(String token) {
        return parse(token).map(c -> Objects.nonNull(c.getSubject())).orElse(false);
    }

    public long expirationSeconds() {
        return expiration.toSeconds();
    }

    public Optional<String> extractJti(String token) {
        return parse(token).map(Claims::getId);
    }

    public long getRemainingSeconds(String token) {
        return parse(token)
                .map(c -> c.getExpiration().toInstant().getEpochSecond() - Instant.now().getEpochSecond())
                .orElse(0L);
    }
}
