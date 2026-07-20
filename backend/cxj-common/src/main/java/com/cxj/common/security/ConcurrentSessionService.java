package com.cxj.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 并发登录控制：限制同一账号的最大并发登录数，超限时踢掉最早的 token
 */
@Service
@RequiredArgsConstructor
public class ConcurrentSessionService {

    private static final String KEY_PREFIX = "auth:token:";
    private static final String BLACKLIST_PREFIX = "auth:blacklist:";

    private final StringRedisTemplate redisTemplate;

    @Value("${app.security.max-sessions-per-user:3}")
    private int maxSessions;

    /**
     * 注册新 token，如果超过最大并发数则踢掉最早的
     */
    public void registerToken(Long userId, String jti, long ttlSeconds) {
        String key = KEY_PREFIX + userId;
        SetOperations<String, String> ops = redisTemplate.opsForSet();

        ops.add(key, jti);
        redisTemplate.expire(key, Duration.ofSeconds(ttlSeconds));

        Long size = ops.size(key);
        if (size != null && size > maxSessions) {
            Set<String> all = ops.members(key);
            if (all != null) {
                List<String> members = new ArrayList<>(all);
                int toRemove = (int) (size - maxSessions);
                for (int i = 0; i < toRemove && i < members.size(); i++) {
                    String removedJti = members.get(i);
                    ops.remove(key, removedJti);
                    redisTemplate.opsForValue().set(
                            BLACKLIST_PREFIX + removedJti,
                            "kicked",
                            Duration.ofSeconds(ttlSeconds)
                    );
                }
            }
        }
    }

    /**
     * 移除 token（登出时调用）
     */
    public void removeToken(Long userId, String jti) {
        redisTemplate.opsForSet().remove(KEY_PREFIX + userId, jti);
    }

    /**
     * 使该用户所有 token 失效（修改密码时调用）
     */
    public void invalidateAll(Long userId) {
        String key = KEY_PREFIX + userId;
        Set<String> jtis = redisTemplate.opsForSet().members(key);
        if (jtis != null) {
            for (String jti : jtis) {
                redisTemplate.opsForValue().set(
                        BLACKLIST_PREFIX + jti,
                        "invalidated",
                        Duration.ofDays(1)
                );
            }
        }
        redisTemplate.delete(key);
    }
}
