# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 3.5 + JDK 21 REST API shopping mall backend example with SQLite database.

## Common Commands

```bash
# Build the project
mvn clean package

# Run the application
cd app && mvn spring-boot:run

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=UserServiceTest
```

## Architecture

### Module Structure
```
shopping-mall/
├── facade/       # RPC 接口层（二方包，无依赖）
├── domain/       # 业务领域层（聚合所有业务代码）
├── starter/      # 自定义 Spring Boot Starter
├── web/          # HTTP 接口层（Controller）
└── app/          # 主应用入口
```

### Module Dependencies
```
app -> web -> domain -> facade
       |              |
       └── starter ───┘
```

### Module Responsibilities

- **facade**: RPC 接口定义层，无任何依赖，可独立打包为二方包
  - RPC 接口定义（UserRpcService, ProductRpcService, OrderRpcService）
  - 独立 DTO 对象（UserDTO, ProductDTO, OrderDTO 等）
  - 枚举定义（OrderStatus）

- **domain**: 业务领域层，实现 facade 接口，包含所有业务代码
  - `common/`: Entity、Repository、DTO、异常
  - `user/`: 用户服务
  - `product/`: 商品服务
  - `order/`: 订单服务、邮件服务
  - `domain/impl/`: RpcService 实现

- **starter**: 自定义 Spring Boot Starter 示例
  - GreetingService 演示自动配置

- **web**: HTTP 接口层
  - 所有 Controller（UserController, ProductController, OrderController, GreetingController）
  - 调用 domain 层的 RpcService

- **app**: 主应用入口
  - Spring Boot 启动类
  - 配置文件

### Layer Structure
- **Controller** - REST endpoints (web module)
- **RpcService** - RPC interface implementation (domain module)
- **Service** - Business logic (domain module)
- **Repository** - Data access via JPA (domain module)
- **Entity** - JPA entities (domain module)

### Key Configuration
- Database: SQLite (`shopping.db`), HikariCP (max 10 connections)
- Cache: Caffeine (max 1000 entries, 5min TTL)
- API Docs: SpringDoc OpenAPI at `/swagger-ui.html`
- JPA `ddl-auto: update` - auto-create/update tables

## Important Notes

- SQLite dialect: `org.hibernate.community.dialect.SQLiteDialect`
- Email is simulated (no real SMTP), check logs for confirmation
- `schema.sql` and `data.sql` execute on every startup

## Code Standards

### Dependency Injection
- Use **constructor injection** instead of field injection
- Required: `private final SomeService someService;` + constructor