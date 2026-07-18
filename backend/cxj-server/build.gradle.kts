// cxj-server: 启动层 — Spring Boot 启动类、全局配置、配置文件
plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":cxj-module-system"))
    implementation(project(":cxj-security"))  // SecurityConfig / MybatisPlusConfig 直接引用

    // Spring Boot Web
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "commons-logging", module = "commons-logging")
        exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
    }

    // Spring Security (SecurityConfig)
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Redis + Cache (RedisConfig)
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.apache.commons:commons-pool2")

    // MyBatis-Plus (MybatisPlusConfig)
    implementation("com.baomidou:mybatis-plus-spring-boot4-starter:3.5.17")
    implementation("com.baomidou:mybatis-plus-jsqlparser:3.5.17")

    // Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // PostgreSQL Driver
    runtimeOnly("org.postgresql:postgresql:42.7.13")

    // OpenAPI (OpenApiConfig)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // Configuration Processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("cxj.jar")
}
