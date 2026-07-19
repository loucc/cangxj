package com.cxj.common;

import com.cxj.common.enums.ResultCode;
import com.cxj.common.exception.BusinessException;
import com.cxj.common.exception.GlobalExceptionHandler;
import com.cxj.common.response.R;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基础组件单元测试 — 不依赖 Spring Context
 */
class CommonComponentTest {

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
    void globalExceptionHandler_shouldHandleBusinessException() {
        var handler = new GlobalExceptionHandler();
        var ex = new BusinessException(ResultCode.LOGIN_FAILED);

        var response = handler.handleBusiness(ex, new org.springframework.mock.web.MockHttpServletRequest());

        assertEquals(200, response.getStatusCode().value());
        assertEquals(ResultCode.LOGIN_FAILED.getCode(), response.getBody().code());
    }

    @Test
    void webMvcConfig_shouldCreateCustomizer() {
        var config = new com.cxj.config.WebMvcConfig();
        var customizer = config.customDateTimeFormat();
        assertNotNull(customizer);
    }
}
