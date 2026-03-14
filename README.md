# 网购商城示例工程 (Shopping Mall)

基于 Spring Boot 3.2 + JDK 21 的网购商城后端 REST API 示例工程。

## 技术栈

| 技术 | 版本 |
|------|------|
| Spring Boot | 3.2.5 |
| JDK | 21 |
| Maven | - |
| SQLite | 3.45.1.0 |
| HikariCP | - |
| Spring Cache (Caffeine) | - |
| SpringDoc OpenAPI | 2.3.0 |
| Gatling | 3.13.5 |
| Scala | 2.13.16 |

## 功能模块

- **用户管理** - 用户注册、查询
- **商品管理** - 商品CRUD、库存管理、缓存
- **订单系统** - 创建订单、自动扣库存、状态流转
- **邮件通知** - 异步模拟邮件发送
- **API文档** - Swagger UI

## 快速开始

### 1. 启动应用

```bash
cd shopping-mall
mvn spring-boot:run
```

### 2. 访问地址

| 服务 | 地址 |
|------|------|
| API根地址 | http://localhost:8080 |
| Swagger文档 | http://localhost:8080/swagger-ui.html |
| API JSON | http://localhost:8080/v3/api-docs |

## API接口

### 用户管理
- `GET /api/users` - 获取所有用户
- `GET /api/users/{id}` - 获取用户
- `POST /api/users` - 创建用户

### 商品管理
- `GET /api/products` - 获取所有商品
- `GET /api/products/{id}` - 获取商品
- `GET /api/products/category/{category}` - 按分类查询
- `POST /api/products` - 创建商品
- `PUT /api/products/{id}` - 更新商品
- `DELETE /api/products/{id}` - 删除商品

### 订单管理
- `GET /api/orders` - 获取所有订单
- `GET /api/orders/{id}` - 获取订单
- `GET /api/orders/user/{userId}` - 获取用户订单
- `POST /api/orders` - 创建订单（自动扣库存）
- `PUT /api/orders/{id}/status` - 更新订单状态
- `POST /api/orders/{id}/cancel` - 取消订单

## 示例数据

- 用户: 20条
- 商品: 50条
- 订单: 30条

## 性能测试 (Gatling)

### 使用 Gatling 进行压力测试

#### 1. 启动应用

```bash
mvn spring-boot:run
```

#### 2. 运行性能测试

```bash
# 在另一个终端运行
mvn gatling:test
```

#### 3. 查看报告

```bash
# 打开 HTML 报告
start target/gatling/shoppingmallsimulation-*/index.html
```

### 测试场景 (高负载 100x)

| 场景 | 用户数 | 描述 |
|------|--------|------|
| Browse Products | 1000 | 商品浏览 (每个用户3次列表+1次详情) |
| Query Orders | 500 | 订单查询 (每个用户5次查询+1次全量) |
| Mixed Workflow | 200 | 混合购物流程 |
| Register User | 200 | 用户注册 |
| Create Order | 100 | 创建订单 |

**总用户数: 2000**

### 断言验证

| 断言条件 | 目标值 |
|----------|--------|
| P95响应时间 | < 3000 ms |
| 成功率 | > 80% |
| 最大响应时间 | < 15000 ms |

### 性能测试结果

详见 [performance-test-report.md](performance-test-report.md)

#### 关键发现

- **SQLite 并发限制**: SQLite 是单文件数据库，在高并发场景下存在写锁独占性问题
- **纯读接口正常**: 商品列表、订单列表等纯读接口在测试中表现稳定
- **建议**: 对于生产环境高并发场景，建议迁移到 PostgreSQL 或 MySQL

#### 性能指标参考

| 并发级别 | 表现 | 建议 |
|----------|------|------|
| < 10用户 | ✅ 正常 | 适合开发测试 |
| 10-100用户 | ⚠️ 部分超时 | 需要优化配置 |
| > 100用户 | ❌ 大量失败 | 不建议使用 SQLite |

## 项目结构

```
shopping-mall/
├── pom.xml
├── PRD.md                      # 项目需求文档
├── README.md                   # 本文件
├── performance-test-report.md  # 性能测试报告
├── src/
│   ├── main/
│   │   ├── java/com/example/shopping/
│   │   │   ├── ShoppingApplication.java
│   │   │   ├── config/         # 配置类
│   │   │   ├── controller/     # REST控制器
│   │   │   ├── service/        # 业务逻辑
│   │   │   ├── repository/     # 数据访问
│   │   │   ├── entity/         # 实体类
│   │   │   ├── dto/            # 数据传输对象
│   │   │   └── exception/      # 异常处理
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── schema.sql
│   │       └── data.sql        # 示例数据
│   └── test/
│       ├── java/               # 单元测试
│       └── scala/              # Gatling性能测试
│           └── com/example/shopping/perf/
│               └── ShoppingMallSimulation.scala
└── target/gatling/             # 性能测试报告
    └── shoppingmallsimulation-*/
        └── index.html
```

## 运行要求

- JDK 21+
- Maven 3.6+
- 端口 8080 未被占用

## 许可证

MIT