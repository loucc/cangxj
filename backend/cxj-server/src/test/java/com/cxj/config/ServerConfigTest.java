package com.cxj.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Server 层配置类单元测试 — 不依赖 Spring Context
 */
class ServerConfigTest {

    @Test
    void openApiConfig_shouldBuildWithoutError() {
        var config = new OpenApiConfig();
        var openAPI = config.openAPI();
        assertNotNull(openAPI);
        assertEquals("cxj API", openAPI.getInfo().getTitle());
    }

    @Test
    void redisConfig_shouldBuildCacheManager() {
        var config = new RedisConfig();
        assertNotNull(config);
    }
}
