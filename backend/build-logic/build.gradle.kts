plugins {
    `kotlin-dsl`
}

dependencies {
    // Spring Boot Gradle 插件，供 convention plugins 使用
    // 版本必须与 libs.versions.toml 中的 spring-boot 版本一致
    implementation("org.springframework.boot:spring-boot-gradle-plugin:4.1.0")
}
