package com.cxj.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.List;
import java.util.Map;

/**
 * 代码生成器（独立 main() 工具，compileOnly 不进入生产包）
 * <p>
 * 用法：在 IDE 中直接运行 main()，修改 TABLES 数组指定要生成的表名。
 * 连接本地 PostgreSQL，读取表结构元数据，生成完整 CRUD 代码到 src/main/java。
 */
public class CodeGenerator {

    // ====== 数据库连接配置（PostgreSQL 18）======
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/ry-vue?currentSchema=public&stringtype=unspecified";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "root";

    // ====== 项目配置 ======
    private static final String BASE_PACKAGE = "com.cxj";
    private static final String AUTHOR = "cangxj";
    private static final String TABLE_PREFIX = "sys_";

    // ====== 要生成的表名列表（修改此处后运行 main()，默认不覆盖已有文件）======
    private static final String[] TABLES = {"user_role"};

    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String javaOutputDir = projectPath + "/src/main/java";

        for (String table : TABLES) {
            // 从表名派生模块名：sys_order → order, sys_role → role
            String moduleName = table.startsWith(TABLE_PREFIX)
                    ? table.substring(TABLE_PREFIX.length())
                    : table;

            System.out.println(">>> 生成表 [" + table + "] → 模块 [" + moduleName + "]");

            FastAutoGenerator.create(DB_URL, DB_USERNAME, DB_PASSWORD)
                    .globalConfig(builder -> builder
                            .author(AUTHOR)
                            .outputDir(javaOutputDir)
                            .disableOpenDir()
                    )
                    .packageConfig(builder -> builder
                            .parent(BASE_PACKAGE)
                            .moduleName(moduleName)
                            .entity("entity")
                            .mapper("mapper")
                            .service("service")
                            .serviceImpl("service")
                            .controller("controller")
                    )
                    .strategyConfig(builder -> builder
                            .addInclude(table)
                            .addTablePrefix(TABLE_PREFIX)
                            // Entity 策略
                            .entityBuilder()
                            .enableLombok()
                            .enableTableFieldAnnotation()
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .idType(com.baomidou.mybatisplus.annotation.IdType.ASSIGN_ID)
                            .logicDeleteColumnName("deleted")
                            .versionColumnName("version")
                            // Mapper 策略
                            .mapperBuilder()
                            .enableBaseResultMap()
                            .enableBaseColumnList()
                            // Service 策略
                            .serviceBuilder()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl")
                            // Controller 策略
                            .controllerBuilder()
                            .enableRestStyle()
                    )
                    .templateConfig(builder -> builder
                            // 禁用所有内置模板，全部使用自定义模板
                            .disable()
                    )
                    .injectionConfig(builder -> builder
                            .customMap(Map.of(
                                    "basePackage", BASE_PACKAGE,
                                    "moduleName", moduleName,
                                    "author", AUTHOR))
                            .customFile(buildCustomFiles())
                    )
                    .templateEngine(new FreemarkerTemplateEngine())
                    .execute();
        }

        System.out.println(">>> 代码生成完成！");
    }

    private static List<CustomFile> buildCustomFiles() {
        String tplDir = "/generator/templates/";
        return List.of(
                // Entity
                new CustomFile.Builder()
                        .fileName(".java")
                        .templatePath(tplDir + "entity.java.ftl")
                        .packageName("entity")
                        .build(),
                // Mapper
                new CustomFile.Builder()
                        .fileName("Mapper.java")
                        .templatePath(tplDir + "mapper.java.ftl")
                        .packageName("mapper")
                        .build(),
                // Service
                new CustomFile.Builder()
                        .fileName("Service.java")
                        .templatePath(tplDir + "service.java.ftl")
                        .packageName("service")
                        .build(),
                // ServiceImpl
                new CustomFile.Builder()
                        .fileName("ServiceImpl.java")
                        .templatePath(tplDir + "serviceImpl.java.ftl")
                        .packageName("service")
                        .build(),
                // Controller
                new CustomFile.Builder()
                        .fileName("Controller.java")
                        .templatePath(tplDir + "controller.java.ftl")
                        .packageName("controller")
                        .build(),
                // CreateDTO
                new CustomFile.Builder()
                        .fileName("CreateDTO.java")
                        .templatePath(tplDir + "createDTO.java.ftl")
                        .packageName("controller/dto")
                        .build(),
                // UpdateDTO
                new CustomFile.Builder()
                        .fileName("UpdateDTO.java")
                        .templatePath(tplDir + "updateDTO.java.ftl")
                        .packageName("controller/dto")
                        .build(),
                // QueryDTO
                new CustomFile.Builder()
                        .fileName("QueryDTO.java")
                        .templatePath(tplDir + "queryDTO.java.ftl")
                        .packageName("controller/dto")
                        .build(),
                // VO
                new CustomFile.Builder()
                        .fileName("VO.java")
                        .templatePath(tplDir + "vo.java.ftl")
                        .packageName("controller/vo")
                        .build(),
                // MapStruct Converter
                new CustomFile.Builder()
                        .fileName("Converter.java")
                        .templatePath(tplDir + "converter.java.ftl")
                        .packageName("converter")
                        .build(),
                // Mapper XML（输出到 resources/mapper/）
                new CustomFile.Builder()
                        .fileName("Mapper.xml")
                        .templatePath(tplDir + "mapper.xml.ftl")
                        .filePath(System.getProperty("user.dir") + "/src/main/resources/mapper")
                        .build()
        );
    }
}
