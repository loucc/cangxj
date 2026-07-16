package com.cxj.common.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 基于 Redis INCR + EXPIRE 的滑动窗口限流服务。
 * <p>
 * 支持 IP 和用户名两个维度，用于登录接口的防暴力破解。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.rate-limit.login.max-attempts:5}")
    private int maxAttempts;

    @Value("${app.rate-limit.login.window-seconds:60}")
    private int windowSeconds;

    private static final String LOGIN_RATE_LIMIT_PREFIX = "rate-limit:login:";

    /**
     * 检查指定 key 是否超过限流阈值。
     *
     * @param identifier 标识（如 IP 或用户名）
     * @return true 表示已被限流，应拒绝请求
     */
    public boolean isRateLimited(String identifier) {
        String key = LOGIN_RATE_LIMIT_PREFIX + identifier;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
        }
        boolean limited = count != null && count > maxAttempts;
        if (limited) {
            log.warn("[RateLimit] identifier={}, attempts={}, window={}s", identifier, count, windowSeconds);
        }
        return limited;
    }

    /**
     * 登录成功后重置计数。
     */
    public void resetRateLimit(String identifier) {
        redisTemplate.delete(LOGIN_RATE_LIMIT_PREFIX + identifier);
    }

    /**
     * 获取剩余可用次数。
     */
    public long getRemainingAttempts(String identifier) {
        String key = LOGIN_RATE_LIMIT_PREFIX + identifier;
        String count = redisTemplate.opsForValue().get(key);
        if (count == null) {
            return maxAttempts;
        }
        return Math.max(0, maxAttempts - Long.parseLong(count));
    }
}
