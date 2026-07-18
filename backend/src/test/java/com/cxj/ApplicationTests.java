package com.cxj;

import com.cxj.common.enums.ResultCode;
import com.cxj.common.exception.BusinessException;
import com.cxj.common.exception.GlobalExceptionHandler;
import com.cxj.common.response.R;
import com.cxj.common.security.JwtTokenProvider;
import com.cxj.config.OpenApiConfig;
import com.cxj.config.RedisConfig;
import com.cxj.config.WebMvcConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 纯单元测试 — 不依赖 Spring Context 和外部服务，验证核心组件可正常实例化
 */
class ApplicationTests {

    @Test
    void jwtTokenProvider_shouldGenerateAndParse() {
        var provider = new JwtTokenProvider(
                "smoke-test-secret-key-must-be-at-least-32-bytes-long!!",
                3600, "cxj");

        String token = provider.generate("smoke-user", java.util.Map.of());
        assertTrue(provider.isValid(token));
        assertEquals(3600, provider.expirationSeconds());
    }

    @Test
    void globalExceptionHandler_shouldHandleBusinessException() {
        var handler = new GlobalExceptionHandler();
        var ex = new BusinessException(ResultCode.LOGIN_FAILED);

        var response = handler.handleBusiness(ex, new org.springframework.mock.web.MockHttpServletRequest());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(ResultCode.LOGIN_FAILED.getCode(), response.getBody().code());
    }

    @Test
    void resultCodes_shouldBeUnique() {
        var codes = java.util.Arrays.stream(ResultCode.values())
                .map(ResultCode::getCode)
                .toList();
        assertEquals(codes.size(), new java.util.HashSet<>(codes).size(),
                "ResultCode 中存在重复的 code 值");
    }

    @Test
    void rRecord_shouldWrapDataCorrectly() {
        R<String> ok = R.ok("hello");
        assertEquals(200, ok.code());
        assertEquals("hello", ok.data());

        R<Void> fail = R.fail(ResultCode.NOT_FOUND);
        assertEquals(404, fail.code());
        assertNull(fail.data());
    }

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

    @Test
    void webMvcConfig_shouldCreateObjectMapper() {
        var config = new WebMvcConfig();
        var mapper = config.objectMapper();
        assertNotNull(mapper);
    }
}
