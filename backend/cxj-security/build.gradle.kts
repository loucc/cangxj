// cxj-security: 安全框架层 — Spring Security、JWT、权限上下文
dependencies {
    implementation(project(":cxj-common"))

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Spring Web (OncePerRequestFilter, ObjectMapper)
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Jackson (ObjectMapper for JSON error responses)
    implementation("com.fasterxml.jackson.core:jackson-databind")

    // Redis (TokenBlacklist, RefreshToken, ConcurrentSession, RateLimit)
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.apache.commons:commons-pool2")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")
}
