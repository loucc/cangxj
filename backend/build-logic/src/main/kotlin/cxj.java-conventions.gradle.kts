import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

plugins {
    java
}

group = "com.cxj"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val springBootBom = "org.springframework.boot:spring-boot-dependencies:${libs.findVersion("spring-boot").get()}"

dependencies {
    // Spring Boot BOM via 原生 platform()
    implementation(platform(springBootBom))
    compileOnly(platform(springBootBom))
    annotationProcessor(platform(springBootBom))
    testImplementation(platform(springBootBom))
    testCompileOnly(platform(springBootBom))
    testAnnotationProcessor(platform(springBootBom))
    testRuntimeOnly(platform(springBootBom))

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // MapStruct
    implementation(libs.findLibrary("mapstruct").get())
    annotationProcessor(libs.findLibrary("mapstruct-processor").get())
    annotationProcessor(libs.findLibrary("lombok-mapstruct-binding").get())

    // Test
    testImplementation(libs.findLibrary("spring-boot-starter-test").get())
    testRuntimeOnly(libs.findLibrary("junit-platform-launcher").get())
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
