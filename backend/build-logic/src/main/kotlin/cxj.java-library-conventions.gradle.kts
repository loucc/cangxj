import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

plugins {
    id("cxj.java-conventions")
    `java-library`
}

// 为 api 配置也添加 BOM
val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val springBootBom = "org.springframework.boot:spring-boot-dependencies:${libs.findVersion("spring-boot").get()}"

dependencies {
    "api"(platform(springBootBom))
}
