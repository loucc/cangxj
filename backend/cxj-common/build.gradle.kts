// cxj-common: 基础层 — 统一响应、异常、枚举、工具类
dependencies {
    // MyBatis-Plus (for IPage in PageResult)
    implementation("com.baomidou:mybatis-plus-spring-boot4-starter:3.5.17")

    // Jackson (for @JsonInclude, ObjectMapper in WebMvcConfig)
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Spring Security (GlobalExceptionHandler handles security exceptions)
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Validation (GlobalExceptionHandler handles validation exceptions)
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Web (for @RestControllerAdvice, HttpServletRequest)
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Hutool
    implementation("cn.hutool:hutool-all:5.8.46")
}
