package com.cxj.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.module.SimpleModule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson ObjectMapper 定制配置
 * <p>
 * Spring Boot 4.x 使用 Jackson 3.x，推荐通过 {@link JsonMapperBuilderCustomizer}
 * 定制自动配置的 ObjectMapper，而非直接创建 @Primary Bean。
 * java.time 支持已内置于 databind（ext.javatime 包）。
 */
@Configuration
public class WebMvcConfig {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public JsonMapperBuilderCustomizer customDateTimeFormat() {
        return builder -> {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
            SimpleModule javaTimeModule = new SimpleModule("JavaTimeModule");
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(fmt));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(fmt));
            builder.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
            builder.addModule(javaTimeModule);
        };
    }
}
