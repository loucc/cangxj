package com.cxj;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 测试专用 Spring Boot 启动配置类。
 * <p>
 * 仅为 @WebMvcTest 等切片测试提供 @SpringBootConfiguration，
 * 不用于生产环境（生产启动类在 cxj-admin 模块）。
 */
@SpringBootApplication
class TestApplication {
}
