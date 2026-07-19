plugins {
    id("cxj.spring-boot-conventions")
}

dependencies {
    implementation(project(":cxj-module-system"))
    implementation(project(":cxj-security"))  // SecurityConfig / MybatisPlusConfig 直接引用

    // Spring Boot Web
    implementation(libs.spring.boot.starter.web) {
        exclude(group = "commons-logging", module = "commons-logging")
        exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
    }

    // Spring Security (SecurityConfig)
    implementation(libs.spring.boot.starter.security)

    // Actuator
    implementation(libs.spring.boot.starter.actuator)

    // Redis + Cache (RedisConfig)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.cache)
    implementation(libs.commons.pool2)

    // MyBatis-Plus (MybatisPlusConfig)
    implementation(libs.mybatis.plus.spring.boot4.starter)
    implementation(libs.mybatis.plus.jsqlparser)

    // Flyway
    implementation(libs.bundles.flyway)

    // PostgreSQL Driver
    runtimeOnly(libs.postgresql)

    // OpenAPI (OpenApiConfig)
    implementation(libs.springdoc.openapi)

    // Configuration Processor
    annotationProcessor(libs.spring.boot.configuration.processor)

    // Test
    testImplementation(libs.spring.boot.starter.test)
}
