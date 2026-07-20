package com.cxj.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Token 黑名单服务：支持主动使已签发的 token 失效（登出 / 修改密码）
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String KEY_PREFIX = "auth:blacklist:";

    private final StringRedisTemplate redisTemplate;
    private final JwtTokenProvider tokenProvider;

    /**
     * 将 token 加入黑名单（登出 / 修改密码时调用）
     */
    public void blacklist(String token) {
        tokenProvider.extractJti(token).ifPresent(jti -> {
            long ttl = tokenProvider.getRemainingSeconds(token);
            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                        KEY_PREFIX + jti,
                        "1",
                        Duration.ofSeconds(ttl)
                );
            }
        });
    }

    /**
     * 检查 token 是否在黑名单中
     */
    public boolean isBlacklisted(String token) {
        return tokenProvider.extractJti(token)
                .map(jti -> Boolean.TRUE.equals(
                        redisTemplate.hasKey(KEY_PREFIX + jti)))
                .orElse(false);
    }
}
