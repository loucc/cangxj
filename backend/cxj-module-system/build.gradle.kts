plugins {
    id("cxj.java-conventions")
}

dependencies {
    implementation(project(":cxj-common"))
    implementation(project(":cxj-security"))

    // Spring Cache (@Cacheable, @CacheEvict)
    implementation(libs.spring.boot.starter.cache)

    // OpenAPI / Swagger
    implementation(libs.springdoc.openapi)

    // MyBatis-Plus (entity, mapper, service)
    implementation(libs.mybatis.plus.spring.boot4.starter)

    // Test: WebMvcTest + Security Test
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.spring.security.test)
}
