# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 3.5 + JDK 25 REST API shopping mall backend example with SQLite database. The project is a multi-module Maven project designed as a teaching example demonstrating clean architecture, design patterns, and Spring Boot best practices.

## Common Commands

```bash
# Build the project (skip tests for speed)
mvn clean package -DskipTests

# Full build with tests
mvn clean verify

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=UserServiceTest

# Run a single test method
mvn test -Dtest=UserServiceTest#testCreateUser

# Run tests in a specific module
mvn test -pl domain -am

# Run Gatling performance tests (app must be running on port 8080)
mvn gatling:test -pl app

# Run the application (dev profile default)
cd app && mvn spring-boot:run

# Run with prod profile
cd app && mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Check dependency tree
mvn dependency:tree
```

## Architecture

### Module Structure (5 modules)

```
shopping-mall/                    (parent POM, packaging: pom)
├── facade/                       (RPC 接口层 — 二方包, no Spring dependencies)
├── domain/                       (业务领域层 — business logic, JPA, caching)
├── starter/                      (自定义 Spring Boot Starter)
├── web/                          (HTTP 接口层 — Controllers, REST concerns)
└── app/                          (主应用入口 — Spring Boot main class, config)
```

### Module Dependencies

```
facade (jakarta.validation-api only)
   └── domain (facade + JPA + cache + validation + MapStruct)
            ├── web (domain + spring-web + springdoc + spring-mail)
            └── starter (独立 Spring Boot starter)
                     └── app (web + starter + actuator)
```

### Module Responsibilities

- **facade**: RPC 接口 + DTO 定义，零依赖，可独立发布为二方包
- **domain**: 纯业务逻辑 + JPA 实体/Repository + 领域 DTO。不依赖 web/springdoc/mail
- **starter**: 自定义自动配置示例（GreetingService）
- **web**: Controller + 全局异常处理 + 基础设施适配器（邮件发送）。Web/HTTP 关注点全在此层
- **app**: Spring Boot 启动类 + 配置文件 + Async/Cache/Swagger 配置

### Layer Structure (inside domain module)

```
common/entity/     — JPA entities (User, Product, Order)
common/repository/ — Spring Data JPA repositories
common/dto/        — Domain-level DTOs (records)
common/exception/  — BusinessException

user/service/      — UserService
product/service/   — ProductService (includes stock/cache logic)
order/service/     — OrderService, OrderDataAccessor
order/state/       — State pattern for order status transitions
order/event/       — Event-driven notifications
order/port/        — Outbound port interfaces (NotificationSender)
domain/impl/       — RpcService implementations (facade ↔ domain adapter)
domain/mapper/     — MapStruct mappers (domain DTO ↔ facade DTO)
```

### Controller → RPC → Service Flow

```
Controller (web)
  └─ calls RpcService interface (facade)
       └─ RpcServiceImpl (domain/domain/impl/) — MapStruct DTO conversion
            └─ Domain Service (domain/user|product|order/service/)
                 └─ Repository (domain/common/repository/)
                      └─ JPA Entity (domain/common/entity/)
```

Controllers only depend on facade interfaces. RpcServiceImpl uses MapStruct for DTO conversion.

### Hexagonal Architecture Elements

- **Ports** (domain): `NotificationSender` interface in `order/port/` defines outbound boundary
- **Adapters** (web): `MailNotificationAdapter` implements `NotificationSender` via `JavaMailSender`
- **Anti-corruption layer** (domain): RpcServiceImpl converts between domain ↔ facade DTOs

## Design Patterns

### State Pattern — Order Status (`order/state/`)

Each `Order.Status` has a dedicated handler (`PendingStateHandler`, `PaidStateHandler`, `ShippedStateHandler`, `DeliveredStateHandler`, `CancelledStateHandler`). `OrderStateHandlerRegistry` collects all handlers and manages transitions.

### Event-Driven Pattern — Order Events (`order/event/`)

Uses Spring's `ApplicationEventPublisher` to fire `OrderCreatedEvent` and `OrderStatusChangedEvent`. `OrderEventListener` asynchronously delegates to `NotificationSender` interface.

### Port & Adapter (Hexagonal) — Notification (`order/port/`)

`NotificationSender` is a domain port interface. `MailNotificationAdapter` (in `web.adapter`) is the infrastructure adapter implementing it using `JavaMailSender`. This follows dependency inversion: domain defines the contract, web provides the implementation.

## Database

- **Runtime**: SQLite via Hibernate `SQLiteDialect`, HikariCP pool size 3
- **Test**: H2 in-memory database
- **DDL**: `spring.jpa.hibernate.ddl-auto=update`
- **Init**: `schema.sql` and `data.sql` execute on every startup

## Caching

Caffeine cache: `userCache` and `productCache`, max 1000 entries, 5-minute write expiry.

## Testing Patterns

### Unit Tests (Mockito)
- `@ExtendWith(MockitoExtension.class)` + `@Mock` fields + constructor injection
- No Spring context for pure service unit tests

### Repository Tests (Spring)
- `@DataJpaTest` in domain module with H2
- `TestApplication.java` (`@SpringBootApplication`) for test context

### Controller Integration Tests (MockMvc)
- `@SpringBootTest` + `@AutoConfigureMockMvc` + `@MockitoBean`
- Tests HTTP status codes, JSON response structure, error handling
- Located in `app/src/test/java/.../controller/`

### Performance Tests
- Gatling in `app/src/test/scala/`, run with `mvn gatling:test -pl app`

## CI/CD

### CI (ci.yml)
Push/PR to main. Runs `mvn clean verify -B` on JDK 21 (Temurin). Codecov upload.

### Release (release.yml)
Tags `v*`. Builds JAR, creates GitHub Release, publishes to GitHub Packages.

## Key Configuration Points

- **app/src/main/resources/application.yml** — Main config
- **app/src/main/resources/application-dev.yml** — Dev profile (debug logging)
- **app/src/main/resources/application-prod.yml** — Prod profile
- **domain/src/test/resources/application-test.yml** — Domain test config
- JPA open-in-view: explicitly disabled
- API docs: `/swagger-ui.html` via SpringDoc OpenAPI (configured in web module)
- Actuator: health, info, beans, conditions, env, metrics, mappings

## Code Standards

- **Constructor injection** (no `@Autowired` field injection)
- **DTOs are Java records** (both facade and domain layers)
- **Entities are classes** with JPA annotations, getters/setters
- **MapStruct** for domain ↔ facade DTO conversion (`domain/mapper/`)
- **Password encoding** via `spring-security-crypto`
- **`@Async`** for fire-and-forget operations (event listeners)
