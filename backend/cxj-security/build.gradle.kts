plugins {
    id("cxj.java-library-conventions")
}

dependencies {
    api(project(":cxj-common"))

    // Spring Security
    api(libs.spring.boot.starter.security)

    // Spring Web (OncePerRequestFilter, ObjectMapper)
    api(libs.spring.boot.starter.web)

    // Redis (TokenBlacklist, RefreshToken, ConcurrentSession, RateLimit)
    api(libs.spring.boot.starter.data.redis)

    implementation(libs.commons.pool2)

    // JWT
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)
}
