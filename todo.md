# TODO

## 阶段 1 多模块拆分 — 状态

### ✅ 已完成

- [x] 拆分为 5 个子模块：cxj-common / cxj-security / cxj-module-system / cxj-server / cxj-generator
- [x] 所有源文件搬迁（零 import 修改）
- [x] 构建文件编写（settings.gradle.kts + root build.gradle.kts + 5 模块 build.gradle.kts）
- [x] 测试文件搬迁 + ApplicationTests 拆分为 CommonComponentTest / ServerConfigTest
- [x] WebMvcConfig 移至 cxj-common（跨模块测试需要）
- [x] 新增 TestApplication 为 @WebMvcTest 提供 @SpringBootConfiguration
- [x] Dockerfile 更新适配多模块构建路径
- [x] CodeGenerator 输出路径更新指向 cxj-module-system
- [x] `./gradlew clean build` 全量编译 + 测试通过
- [x] bootJar 生成验证（cxj.jar 56MB，不含 generator）
- [x] Git commit 完成（d9692cf）

### ⬜ 未完成

- [ ] **git push 推送** — 网络错误（`HTTP2 framing layer`），需手动执行 `git push`
- [ ] **IntelliJ IDEA 重新导入** — 打开 `backend/` 目录后点击 "Reload Gradle Project"，让 IDE 识别多模块结构
- [ ] **docker-compose.yml 验证** — `docker compose build` 确认 Dockerfile 多模块构建正常
- [ ] **application.yml 中 mybatis-plus.type-aliases-package** — 当前值 `com.cxj.user.entity`，后续新增模块需扩展为通配符或多个包

## 阶段 2（未来 — 新业务出现时）

- [ ] 当 product/supplier/finance/purchase 代码量 >20 文件时，独立拆为 `cxj-module-xxx`
- [ ] 更新 `settings.gradle.kts` include 新模块
- [ ] 更新 CodeGenerator 输出路径逻辑（按表名路由到对应模块）

## 阶段 3（未来 — 微服务预备）

- [ ] 提取 `cxj-module-system-api`（UserService 接口 + UserVO），实现编译期解耦
- [ ] 评估是否引入 `java-library` 插件用 `api` 配置暴露传递依赖
