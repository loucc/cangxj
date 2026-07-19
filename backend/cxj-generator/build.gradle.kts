plugins {
    id("cxj.java-conventions")
}

dependencies {
    // MyBatis-Plus (for annotation references in generated code)
    implementation(libs.mybatis.plus.spring.boot4.starter)

    // Code Generator
    implementation(libs.mybatis.plus.generator)
    implementation(libs.freemarker)

    // PostgreSQL Driver (generator connects to DB)
    runtimeOnly(libs.postgresql)
}
