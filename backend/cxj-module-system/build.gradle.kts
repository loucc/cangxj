// cxj-module-system: 业务模块 — 用户管理 + 认证
dependencies {
    implementation(project(":cxj-common"))
    implementation(project(":cxj-security"))

    // Spring Security (PasswordEncoder, @PreAuthorize)
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Spring Web (REST controllers)
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Validation (DTO validation)
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Spring Cache (@Cacheable, @CacheEvict)
    implementation("org.springframework.boot:spring-boot-starter-cache")

    // OpenAPI / Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // MyBatis-Plus (entity, mapper, service)
    implementation("com.baomidou:mybatis-plus-spring-boot4-starter:3.5.17")

    // Test: WebMvcTest + Security Test
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.security:spring-security-test")
}
