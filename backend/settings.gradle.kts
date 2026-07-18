pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin/") }
        maven { url = uri("https://maven.aliyun.com/repository/spring-plugin/") }
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "cxj"
