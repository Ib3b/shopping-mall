# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 3.2 + JDK 21 REST API shopping mall backend example with SQLite database.

## Common Commands

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=UserServiceTest

# Run tests with verbose output
mvn test -Dsurefire.useFile=false

# Run performance tests with Gatling
mvn gatling:test

# View Gatling HTML report
start target/gatling/shoppingmallsimulation-*/index.html
```

## Architecture

### Module Structure
```
shopping-mall/
├── common/       # 公共模块（实体、DTO、异常、Repository）
├── facade/       # RPC 服务接口层（二方包，纯接口定义）
├── domain/       # 业务领域层（实现 facade 接口）
├── user/         # 用户服务模块
├── product/      # 商品服务模块
├── order/        # 订单服务模块
├── mystarter/    # 自定义 Starter 示例
├── web/          # HTTP 接口层（聚合所有 Controller）
└── app/          # 主应用入口
```

### Module Dependencies
```
app -> web -> domain -> facade -> user/product/order -> common
```

### Module Responsibilities
- **facade**: 纯接口定义层，无任何依赖，可独立打包为二方包
  - 包含 RPC 接口定义（UserRpcService, ProductRpcService, OrderRpcService）
  - 包含独立的 DTO 对象（UserDTO, ProductDTO, OrderDTO 等）
  - 包含枚举定义（OrderStatus）

- **domain**: 业务领域层，实现 facade 接口
  - 实现 RPC 服务接口
  - 调用各服务模块（user/product/order）
  - DTO 转换（facade DTO <-> common DTO）

- **web**: HTTP 接口层，聚合所有 Controller
  - 对外提供 REST API
  - 调用 domain 层的 RpcService

- **user/product/order**: 业务服务模块
  - 包含 Service 层业务逻辑
  - 依赖 common 模块

- **common**: 公共基础设施层
  - 实体（Entity）
  - 通用 DTO（Request/Response）
  - Repository 接口
  - 异常定义

### Layer Structure
- **Controller** - REST endpoints, request/response handling
- **Service** - Business logic, transaction management
- **Repository** - Data access via JPA
- **Entity** - JPA entities mapped to database tables
- **DTO** - Data transfer objects for API

### Key Configuration
- Database: SQLite (`shopping.db`), managed by HikariCP (max 10 connections)
- Cache: Caffeine (max 1000 entries, 5min TTL)
- API Docs: SpringDoc OpenAPI at `/swagger-ui.html`

### SQL Scripts Execution
`schema.sql` and `data.sql` execute on every startup (`mode: always`), which overwrites any manual changes. This is intentional for development/testing.

### Core Modules
- **User**: Registration and query
- **Product**: CRUD, inventory management, caching
- **Order**: Create order (auto deducts inventory), status management
- **Mail**: Async simulated email sending

### RPC Services (facade module)
- **UserRpcService**: 用户相关 RPC 接口
- **ProductRpcService**: 商品相关 RPC 接口
- **OrderRpcService**: 订单相关 RPC 接口

## Important Notes

- JPA `ddl-auto: update` is used - Hibernate will auto-create/update tables based on entity annotations
- SQLite dialect: `org.hibernate.community.dialect.SQLiteDialect`
- Email is simulated (no real SMTP), check logs for "sending" confirmation

## Code Standards

### Dependency Injection
- Use **constructor injection** instead of field injection
- Required: `private final SomeService someService;` and constructor
- Optional: Use `@Nullable` + constructor for optional dependencies

---

## Architecture History

### 2026-03-14 重构完成
新增 facade 和 domain 模块，优化多模块架构：
- facade: 二方包，纯接口定义，无依赖
- domain: 实现 facade 接口，聚合各服务模块
- web: 聚合所有 Controller，统一 HTTP 入口

### 原重构计划
1. 创建 facade 模块，定义 RPC 接口和独立 DTO
2. 创建 domain 模块，实现 facade 接口
3. 创建 web 模块，迁移所有 Controller
4. 更新各模块 pom.xml 依赖
5. 运行测试验证
6. Git 提交