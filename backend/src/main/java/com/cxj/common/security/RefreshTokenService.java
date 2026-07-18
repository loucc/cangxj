package com.cxj.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * Refresh Token 服务：生成不透明 UUID token，存 Redis，一次性使用策略
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String KEY_PREFIX = "auth:refresh:";

    private final StringRedisTemplate redisTemplate;

    @Value("${app.security.refresh-token-ttl:604800}")
    private long refreshTtlSeconds;

    /**
     * 生成 refresh_token 并存入 Redis
     */
    public String createRefreshToken(Long userId) {
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                KEY_PREFIX + refreshToken,
                String.valueOf(userId),
                Duration.ofSeconds(refreshTtlSeconds)
        );
        return refreshToken;
    }

    /**
     * 校验并消费 refresh_token（一次性使用）
     */
    public Optional<Long> validateAndConsume(String refreshToken) {
        String key = KEY_PREFIX + refreshToken;
        String userId = redisTemplate.opsForValue().get(key);
        if (userId != null) {
            redisTemplate.delete(key);
            return Optional.of(Long.parseLong(userId));
        }
        return Optional.empty();
    }

    /**
     * 撤销 refresh_token（登出时调用）
     */
    public void revoke(String refreshToken) {
        if (refreshToken != null) {
            redisTemplate.delete(KEY_PREFIX + refreshToken);
        }
    }

    public long getRefreshTtlSeconds() {
        return refreshTtlSeconds;
    }
}
