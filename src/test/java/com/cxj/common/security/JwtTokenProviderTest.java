package com.cxj.common.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        // 使用足够长的 secret（HMAC-SHA256 需要至少 32 字节）
        tokenProvider = new JwtTokenProvider(
                "test-secret-key-for-unit-testing-at-least-32-bytes-long",
                3600,
                "cxj-test"
        );
    }

    @Test
    void generateAndParse_shouldBeSymmetric() {
        Map<String, Object> claims = Map.of("uid", 123L, "roles", List.of("ROLE_USER"));
        String token = tokenProvider.generate("alice", claims);

        Optional<Claims> parsed = tokenProvider.parse(token);
        assertTrue(parsed.isPresent());
        assertEquals("alice", parsed.get().getSubject());
        assertEquals("cxj-test", parsed.get().getIssuer());
        assertEquals(123L, parsed.get().get("uid", Long.class));
    }

    @Test
    void isValid_shouldReturnTrueForValidToken() {
        String token = tokenProvider.generate("bob", Map.of());
        assertTrue(tokenProvider.isValid(token));
    }

    @Test
    void parse_shouldReturnEmptyForTamperedToken() {
        String token = tokenProvider.generate("alice", Map.of());
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        Optional<Claims> result = tokenProvider.parse(tampered);
        assertTrue(result.isEmpty());
    }

    @Test
    void parse_shouldReturnEmptyForExpiredToken() {
        // 创建一个过期时间为 0 秒的 provider
        JwtTokenProvider expiredProvider = new JwtTokenProvider(
                "test-secret-key-for-unit-testing-at-least-32-bytes-long",
                0,  // 立即过期
                "cxj-test"
        );

        String token = expiredProvider.generate("alice", Map.of());

        // Token 应该立即过期
        Optional<Claims> result = tokenProvider.parse(token);
        assertTrue(result.isEmpty());
    }

    @Test
    void parse_shouldReturnEmptyForNullToken() {
        Optional<Claims> result = tokenProvider.parse(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void parse_shouldReturnEmptyForEmptyToken() {
        Optional<Claims> result = tokenProvider.parse("");
        assertTrue(result.isEmpty());
    }

    @Test
    void parse_shouldReturnEmptyForTokenWithWrongIssuer() {
        JwtTokenProvider otherProvider = new JwtTokenProvider(
                "test-secret-key-for-unit-testing-at-least-32-bytes-long",
                3600,
                "other-issuer"
        );
        String token = otherProvider.generate("alice", Map.of());

        Optional<Claims> result = tokenProvider.parse(token);
        assertTrue(result.isEmpty());
    }

    @Test
    void expirationSeconds_shouldReturnConfiguredValue() {
        assertEquals(3600, tokenProvider.expirationSeconds());
    }
}
