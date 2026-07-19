# TODO

---

## 多人协作基础设施改进

### 🔴 高优先级

- [ ] **CI/CD 流水线** — 添加 GitHub Actions 工作流，每次 push/PR 自动执行 `./gradlew build`，确保编译、测试、代码风格全部通过
- [ ] **代码格式化（Spotless）** — 在 `build-logic` Convention Plugins 中配置 Spotless + Google Java Format，CI 中 `spotlessCheck` 强制检查，未格式化的代码不允许合入
- [ ] **分支策略** — 采用 GitHub Flow（feature branch + PR to main），文档化到 `CONTRIBUTING.md`
- [ ] **移除硬编码密码** — `application.yml` 基础配置中的 DB 密码 `root`、Redis 密码 `ruoyi123` 改为 `${ENV_VAR:default}` 占位符，dev 专用值仅保留在 `application-dev.yml`
- [ ] **集成测试** — 引入 Testcontainers，为 PostgreSQL/Redis 编写集成测试，覆盖核心业务流程
- [ ] **JaCoCo 覆盖率** — 在 Convention Plugins 中配置 JaCoCo，CI 中生成覆盖率报告并设置最低阈值

### 🟡 中优先级

- [ ] **`.editorconfig`** — 统一缩进（4 空格）、行尾（LF）、编码（UTF-8）、文件末尾换行
- [ ] **`CONTRIBUTING.md`** — 编写贡献指南：开发环境搭建、分支策略、代码规范、PR 流程、提交信息规范
- [ ] **Pre-commit hooks** — 配置 Git hooks 或 pre-commit 框架，提交前自动执行 spotlessApply + 编译检查
- [ ] **端口统一** — `application.yml` 用 8089，README/Docker 用 8080，需统一
- [ ] **API 文档注解** — 控制器和 DTO 补充 `@Tag`、`@Operation`、`@Schema` 注解，提升 Swagger 文档质量

### 🟢 低优先级

- [ ] **CHANGELOG.md** — 记录版本变更历史
- [ ] **IDE 共享代码风格** — 导出 IntelliJ IDEA code style XML 或提供 EditorConfig 插件配置
- [ ] **docker-compose 验证** — 确认多模块构建在 Docker 中正常工作
- [ ] **application.yml 中 mybatis-plus.type-aliases-package** — 当前值 `com.cxj.user.entity`，后续新增模块需扩展为通配符或多个包
