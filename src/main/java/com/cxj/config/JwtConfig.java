package com.cxj.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

/**
 * JWT 配置校验：非 dev profile 下 JWT Secret 必须通过环境变量注入
 */
@Slf4j
@Configuration
public class JwtConfig {

    @Bean
    public ApplicationRunner jwtSecretValidator(
            Environment env,
            @Value("${app.security.jwt.secret:}") String jwtSecret) {

        return args -> {
            String[] profiles = env.getActiveProfiles();
            boolean isDev = Arrays.asList(profiles).contains("dev");

            if (!isDev && (jwtSecret == null || jwtSecret.isBlank())) {
                log.error("启动失败：非 dev 环境下必须设置 JWT_SECRET 环境变量");
                log.error("生成命令：openssl rand -base64 48");
                throw new IllegalStateException(
                        "JWT_SECRET is not configured. Set it via environment variable for non-dev profiles.");
            }

            if (isDev && (jwtSecret == null || jwtSecret.isBlank())) {
                log.warn("[dev] JWT Secret 使用开发默认值，请勿在生产环境使用");
            }
        };
    }
}
