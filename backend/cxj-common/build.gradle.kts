plugins {
    id("cxj.java-library-conventions")
}

dependencies {
    // MyBatis-Plus (for IPage in PageResult)
    api(libs.mybatis.plus.spring.boot4.starter)

    // Jackson 3.x (for ObjectMapper, @JsonInclude in public API)
    api(libs.jackson.databind)

    // Spring Security (GlobalExceptionHandler handles security exceptions)
    api(libs.spring.boot.starter.security)

    // Validation (GlobalExceptionHandler handles validation exceptions)
    api(libs.spring.boot.starter.validation)

    // Web (for @RestControllerAdvice, HttpServletRequest)
    api(libs.spring.boot.starter.web)

    // Redis (TokenBlacklist, RefreshToken, ConcurrentSession, RateLimit)
    api(libs.spring.boot.starter.data.redis)
    implementation(libs.commons.pool2)

    // JWT
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)
}
