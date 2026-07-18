# cxj

Spring Boot 4.1 + JDK 25 + MyBatis-Plus + Redis 8.6.3 + PostgreSQL 18 按业务模块分包脚手架。

## 项目结构

```
cangxj/
├── frontend/           // 前端（待开发）
├── backend/            // 后端 Spring Boot 应用
├── docker-compose.yml  // 全栈编排（app + PostgreSQL + Redis）
└── README.md
```

## 后端包结构

```
com.cxj
├── config              // 核心全局配置 (MyBatis-Plus / Redis / Security / VirtualThreads / OpenAPI / WebMvc)
├── common              // 公共组件
│   ├── enums           // ResultCode 等枚举
│   ├── exception       // BusinessException / GlobalExceptionHandler
│   ├── response        // R<T> 与 PageResult<T>
│   ├── security        // JwtTokenProvider / JwtAuthenticationFilter / SecurityResponseHandlers / RateLimitService
│   └── utils           // SecurityUtils
├── user                // 业务模块：用户
│   ├── controller      // 表现层 (UserController)
│   │   ├── dto         // 入参 Record (UserCreateDTO / UserUpdateDTO / UserQueryDTO)
│   │   └── vo          // 返回 Record (UserVO)
│   ├── converter       // MapStruct 对象转换 (UserConverter)
│   ├── service         // 业务层 (UserService + UserServiceImpl)
│   ├── entity          // 数据库实体 (User)
│   └── mapper          // 数据访问 (UserMapper)
├── auth                // 业务模块：认证
│   ├── controller      // 表现层 (AuthController)
│   │   ├── dto         // 入参 Record (LoginDTO)
│   │   └── vo          // 返回 Record (LoginVO)
│   └── service         // 业务层 (AuthService)
├── notification        // 业务模块：通知 (sealed + switch pattern matching)
│   ├── NotificationChannel     // sealed interface (Email / Sms / Webhook)
│   └── NotificationDispatcher  // JDK 25 模式匹配分发
├── generator           // 代码生成器（开发工具，不进入生产包）
└── Application.java
```

## 关键技术选型

| 组件            | 版本 / 说明 |
|-----------------|-------------|
| Spring Boot     | 4.1.0 |
| JDK             | 25 (启用 `spring.threads.virtual.enabled=true`) |
| MyBatis-Plus    | 3.5.17 (`mybatis-plus-spring-boot4-starter`) |
| Redis           | 8.x, Lettuce 客户端；`RedisCacheManager` + `@Cacheable` |
| PostgreSQL      | 18, Hikari 连接池；Flyway 迁移 |
| Security        | JWT (jjwt 0.13.0)，无状态；Redis 滑动窗口登录限流 |
| OpenAPI         | springdoc 2.6，全局 Bearer 认证 |
| 工具            | Hutool 5.8, Lombok, MapStruct 1.6 |

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
   ./gradlew bootJar -x test
   java -jar build/libs/cxj.jar
   ```
5. 访问：
   - Swagger UI: <http://localhost:8080/api/swagger-ui.html>
   - Actuator:   <http://localhost:8080/api/actuator/health>

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
- API: <http://localhost:8080/api>
- Swagger: <http://localhost:8080/api/swagger-ui.html>
- Health: <http://localhost:8080/api/actuator/health>

### 仅构建镜像

```bash
docker build -t cxj:latest ./backend
docker run -d --name cxj-app \
  -p 8080:8080 \
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
