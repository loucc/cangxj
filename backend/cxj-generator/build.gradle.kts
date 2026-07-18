// cxj-generator: 代码生成器（独立工具模块，不参与生产打包）
dependencies {
    // MyBatis-Plus (for annotation references in generated code)
    implementation("com.baomidou:mybatis-plus-spring-boot4-starter:3.5.17")

    // Code Generator
    implementation("com.baomidou:mybatis-plus-generator:3.5.17")
    implementation("org.freemarker:freemarker:2.3.34")

    // PostgreSQL Driver (generator connects to DB)
    runtimeOnly("org.postgresql:postgresql:42.7.13")
}
