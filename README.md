# cxj

Spring Boot 4.1 + JDK 25 + MyBatis-Plus + Redis 8.6.3 + PostgreSQL 18 按业务模块分包脚手架。

## 项目结构

```
cangxj/
├── frontend/           // 前端（待开发）
├── backend/            // 后端 Spring Boot 应用
│   ├── build-logic/    // Convention Plugins（构建约定）
│   ├── gradle/
│   │   └── libs.versions.toml  // 版本目录
│   ├── gradle.properties       // Gradle 全局配置
│   └── ...
├── docker-compose.yml  // 全栈编排（app + PostgreSQL + Redis）
└── README.md
```

## 后端包结构（目标架构）

> 以下为推荐的工程层级规范，🌟 标记为当前尚未实现、后续需要补全的能力。

```
com.cxj
├── common                                 # 通用基础设施
│   ├── base                               # BaseEntity、BaseService、分页封装
│   ├── exception                          # 全局异常处理、BizErrorCode 接口
│   ├── interceptor                        # 拦截器、过滤器
│   ├── util                               # 通用工具（日期、加密、树、IP 等）
│   ├── enums                              # 全局通用枚举（YesNo、DeleteFlag）
│   ├── response                           # 统一返回体 R、分页 VO
│   ├── security                           # 安全上下文、JWT 工具、权限注解
│   ├── lock           🌟                  # 分布式锁注解+AOP
│   ├── cache          🌟                  # 缓存抽象、Key 生成器
│   ├── idempotent     🌟                  # 幂等性注解+拦截器
│   ├── log            🌟                  # 操作日志注解+AOP
│   └── validation     🌟                  # 通用校验器、分组接口
│
├── config                                 # 按中间件分包
│   ├── security                           # Spring Security / Sa-Token 配置
│   ├── mybatis                            # MyBatis-Plus 分页、自动填充、数据权限拦截器
│   ├── redis                              # Redis 序列化、连接池
│   ├── thread                             # 虚拟线程配置、异步线程池
│   ├── web                                # MVC、跨域、静态资源
│   ├── openapi                            # SpringDoc/Swagger 分组
│   ├── scheduling     🌟                  # 定时任务线程池
│   └── jackson        🌟                  # 序列化配置（时间格式、空值忽略）
│
├── modules                                # 业务模块
│   ├── customer       🌟                  # 客户模块（按以下模板）
│   ├── supplier       🌟                  # 供应商
│   ├── product        🌟                  # 商品
│   ├── order          🌟                  # 订单
│   ├── finance        🌟                  # 财务
│   ├── purchase       🌟                  # 采购
│   └── system         🌟                  # 系统管理（用户、角色、菜单、部门）
│       └── ...
│
└── generator                              # 代码生成器，不参与业务
```

### 单个业务模块内部结构（以 order 为例）

```
modules/order
├── controller
│   ├── admin                              # 后台接口
│   └── open                               # 开放接口
├── model
│   ├── entity                             # 数据库实体
│   ├── dto                                # 数据传输对象
│   ├── vo                                 # 视图对象
│   └── enums                              # 模块私有枚举（如 OrderStatusEnum）
├── event              🌟                  # 领域事件定义（OrderCreatedEvent）
├── listener           🌟                  # 事件监听器（可跨模块）
├── converter                              # MapStruct 转换接口
├── mapper                                 # MyBatis-Plus Mapper
├── service
│   └── impl
├── manager                                # 聚合服务 / 防腐层
└── job                🌟                  # 模块私有的定时任务
```

## 关键技术选型

| 组件            | 版本 / 说明 |
|-----------------|-------------|
| Spring Boot     | 4.1.0 |
| JDK             | 25 (启用 `spring.threads.virtual.enabled=true`) |
| Gradle          | 9.6.1 (Kotlin DSL + Convention Plugins + 版本目录) |
| Jackson         | 3.2.1 (databind 包名 `tools.jackson`，注解仍为 `com.fasterxml.jackson`) |
| MyBatis-Plus    | 3.5.17 (`mybatis-plus-spring-boot4-starter`) |
| Redis           | 8.x, Lettuce 客户端；`RedisCacheManager` + `@Cacheable` |
| PostgreSQL      | 18, Hikari 连接池；Flyway 迁移 |
| Security        | JWT (jjwt 0.13.0)，无状态；Redis 滑动窗口登录限流 |
| OpenAPI         | springdoc 2.6，全局 Bearer 认证 |
| 工具            | Lombok, MapStruct 1.6 |

## 快速开始

1. 修改 `backend/src/main/resources/application.yml`：
   - `spring.datasource.*` — PostgreSQL 连接
   - `spring.data.redis.*` — Redis 连接
   - `app.security.jwt.secret` — **生产必须替换**，建议使用环境变量
2. 启动依赖服务：
   ```bash
   # docker 快速拉起（可选）
   docker run -d --name pg -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=cxj -p 5432:5432 postgres:18
   docker run -d --name redis -p 6379:6379 redis:8.6.3
   ```
3. 首次运行：Flyway 自动执行 `V1__init_sys_user.sql`。
4. 构建 & 运行：
   ```bash
   cd backend
   
   # 完整构建（含测试）
   ./gradlew build
   
   # 仅构建 jar（跳过测试）
   ./gradlew bootJar -x test
   
   # 运行
   java -jar cxj-admin/build/libs/cxj.jar
   
   # 配置缓存已启用，二次构建更快
   ```
5. 访问：
   - Swagger UI: <http://localhost:8089/api/swagger-ui.html>
   - Actuator:   <http://localhost:8089/api/actuator/health>

## 认证流程

1. `POST /api/auth/register` 注册用户
2. `POST /api/auth/login` 换取 `accessToken`
3. 后续请求带上 `Authorization: Bearer <token>`

> 登录接口内置 Redis 滑动窗口限流（默认 60s 内最多 5 次），防止暴力破解。可通过 `app.rate-limit.login.*` 调整阈值。

## Docker 部署

项目提供完整的 Docker 支持：多阶段 `Dockerfile` + `docker-compose.yml`（含 PostgreSQL 18 + Redis 8.6.3）。

### 一键启动

```bash
# 1. 准备环境变量（手动创建 .env，参考 docker-compose.yml 中的默认值）
# 生产必须替换 JWT_SECRET / REDIS_PASSWORD 等敏感值
# openssl rand -base64 48

# 2. 构建并启动
docker compose up -d --build

# 3. 查看状态
docker compose ps
docker compose logs -f app

# 4. 停止
docker compose down          # 保留数据卷
docker compose down -v       # 同时清空数据
```

启动后访问：
- API: <http://localhost:8089/api>
- Swagger: <http://localhost:8089/api/swagger-ui.html>
- Health: <http://localhost:8089/api/actuator/health>

### 仅构建镜像

```bash
docker build -t cxj:latest ./backend
docker run -d --name cxj-app \
  -p 8089:8089 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/cxj" \
  -e REDIS_HOST=host.docker.internal \
  -e JWT_SECRET="$(openssl rand -base64 48)" \
  cxj:latest
```

### 关键设计

- **多阶段构建**：`gradle:9.6-jdk25` 编译 → `eclipse-temurin:25-jre-alpine` 运行，镜像 < 250MB。
- **非 root 用户**：容器内以 `app` 用户运行，符合安全基线。
- **JVM 参数**：`-XX:+UseZGC -XX:MaxRAMPercentage=75.0`，JDK 25 默认使用生成式 ZGC。
- **健康检查**：容器与 Compose 均通过 `/actuator/health` 探活。
- **profile 隔离**：`application-docker.yml` 只覆盖连接信息，通过环境变量注入。
- **启动顺序**：`depends_on.condition: service_healthy` 保证 DB/Redis 就绪后才启动 app。
- **数据持久化**：`postgres-data` / `redis-data` / `app-logs` 三个命名卷。

## 分层最佳实践

- **DTO/VO 使用 Record**：不可变、天然 `equals/hashCode`、序列化友好。
- **实体使用 Lombok POJO**：MyBatis-Plus 反射需要可变对象。
- **统一响应**：`R<T>` 包装业务响应，`PageResult<T>` 分页；异常统一由 `GlobalExceptionHandler` 拦截。
- **策略模式**：`sealed interface + record + switch pattern matching`（见 `notification/NotificationDispatcher`）。
- **虚拟线程**：`spring.threads.virtual.enabled=true` + `@Async("applicationTaskExecutor")`；直接使用可注入 `virtualThreadExecutor`。
- **缓存**：`@Cacheable` / `@CacheEvict`，前缀 `cxj:cache:`，默认 30m TTL。
- **审计字段**：`MetaObjectHandler` 自动填充 `createdAt/updatedAt/createdBy/updatedBy`。
- **乐观锁**：`@Version` 字段 + `OptimisticLockerInnerInterceptor`。
- **逻辑删除**：`@TableLogic` 注解，业务无感知。

## 构建系统

项目采用 Gradle 9.6.1 + Kotlin DSL，通过 Convention Plugins 实现构建逻辑复用：

- **`build-logic/`**：包含 3 个 Convention Plugins
  - `cxj.java-conventions`：Java 工具链、BOM 导入、通用依赖（Lombok、MapStruct、测试）
  - `cxj.java-library-conventions`：继承 java-conventions，添加 `java-library` 插件
  - `cxj.spring-boot-conventions`：继承 java-conventions，添加 Spring Boot 插件
- **`gradle/libs.versions.toml`**：版本目录，集中管理所有依赖版本
- **`gradle.properties`**：启用并行构建、构建缓存、配置缓存

### 构建优化

- **并行构建**：`org.gradle.parallel=true`
- **构建缓存**：`org.gradle.caching=true`
- **配置缓存**：`org.gradle.configuration-cache=true`（严格模式）
- **懒加载任务配置**：使用 `configureEach` 替代即时配置

## Jackson 3.x 说明

Spring Boot 4.1.0 默认使用 Jackson 3.x（版本 3.2.1）：

- **包名变更**：`com.fasterxml.jackson.databind.*` → `tools.jackson.databind.*`
- **注解未迁移**：`@JsonInclude`、`@JsonAutoDetect` 等注解仍在 `com.fasterxml.jackson.annotation` 包下
- **java.time 内置**：Jackson 3.x databind 内置 java.time 支持，无需 `jackson-datatype-jsr310`
- **ObjectMapper 构建**：使用 `JsonMapper.builder()` 模式，不再直接 `registerModule()` 或 `disable()`
- **定制方式**：Spring Boot 4.x 推荐使用 `JsonMapperBuilderCustomizer` 定制 ObjectMapper，而非创建 `@Primary` Bean

### 示例：自定义日期格式

```java
@Bean
public JsonMapperBuilderCustomizer customDateTimeFormat() {
    return builder -> {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SimpleModule module = new SimpleModule("JavaTimeModule");
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(fmt));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(fmt));
        builder.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
        builder.addModule(module);
    };
}
```

## 参考

- [Gradle 9 迁移指南](https://docs.gradle.org/9.0/userguide/upgrading_version_8.html)
- [Jackson 3.x Release Notes](https://github.com/FasterXML/jackson/wiki/Jackson-Release-3.0)
- [Spring Boot 4.1 文档](https://docs.spring.io/spring-boot/docs/4.1.0/reference/html/)
