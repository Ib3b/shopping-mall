# 网购商城示例工程 PRD

## 1. 项目概述

- **项目名称**: shopping-mall
- **项目类型**: Spring Boot REST API 示例工程
- **核心功能**: 完整的网购商城后端API，包含用户、商品、订单管理
- **目标用户**: 开发者学习参考

## 2. 技术要求

### 2.1 框架与构建
| 项目 | 要求 |
|------|------|
| Spring Boot | 3.2.x (最新稳定版) |
| JDK | 21 (使用Record、Switch增强、Sequenced Collections) |
| 构建工具 | Maven |
| 数据库 | SQLite (内嵌) |
| 连接池 | HikariCP |

### 2.2 技术特性
- [x] 不使用虚拟线程，使用传统ThreadPoolExecutor
- [x] 使用HikariCP连接池连接SQLite
- [x] 使用Spring Cache + Caffeine本地缓存

### 2.3 禁止项
- 禁止使用虚拟线程 (Virtual Thread)

## 3. 功能模块

### 3.1 用户管理
- [x] 用户注册
- [x] 用户查询
- [x] 简单登录认证

### 3.2 商品管理
- [x] 商品CRUD
- [x] 库存管理
- [x] 缓存优化

### 3.3 订单系统
- [x] 创建订单
- [x] 扣减库存 (事务管理)
- [x] 订单状态流转

### 3.4 邮件通知
- [x] 异步发送邮件 (线程池)
- [x] 模拟邮件 (控制台输出)

### 3.5 API文档
- [x] Swagger UI接入
- [x] 简单认证

## 4. 数据要求

### 4.1 示例数据 (共100条)
| 实体 | 数量 | 说明 |
|------|------|------|
| User | 20条 | user1~user20 |
| Product | 50条 | 多种分类 |
| Order | 30条 | 不同状态 |

## 5. 测试要求

### 5.1 单元测试覆盖
- [x] Repository测试 (@SpringDataJpaTest)
- [x] Service测试 (Mockito)
- [x] Controller测试 (MockMvc)

### 5.2 测试数量
- 完整单元测试覆盖核心业务逻辑

## 6. 配置文件

- application.yml - 主配置
- data.sql - 示例数据初始化

## 7. 检查清单

### 7.1 技术实现
- [ ] Spring Boot 3.2.x
- [ ] JDK 21特性 (Record、Switch、Sequenced Collections)
- [ ] HikariCP连接池 + SQLite
- [ ] 传统ThreadPoolExecutor线程池
- [ ] Caffeine本地缓存

### 7.2 功能模块
- [ ] 用户管理 (注册、查询、登录)
- [ ] 商品管理 (CRUD、库存)
- [ ] 订单系统 (创建订单、扣库存)
- [ ] 邮件通知 (异步、模拟)

### 7.3 数据
- [ ] 100条示例数据 (User 20 + Product 50 + Order 30)

### 7.4 测试
- [ ] Repository测试
- [ ] Service测试
- [ ] Controller测试

### 7.5 文档
- [ ] Swagger UI配置
- [ ] API分组显示
- [ ] 本项目PRD.md

## 8. 项目结构

```
shopping-mall/
├── pom.xml
├── Prd.md
├── src/main/java/com/example/shopping/
│   ├── ShoppingApplication.java
│   ├── config/
│   ├── entity/
│   ├── dto/
│   ├── repository/
│   ├── service/
│   ├── controller/
│   └── exception/
└── src/main/resources/
    ├── application.yml
    └── data.sql
```

## 9. API接口清单

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/users | 用户注册 |
| GET | /api/users/{id} | 获取用户 |
| GET | /api/products | 商品列表 |
| GET | /api/products/{id} | 商品详情 |
| POST | /api/products | 创建商品 |
| PUT | /api/products/{id} | 更新商品 |
| DELETE | /api/products/{id} | 删除商品 |
| POST | /api/orders | 创建订单(扣库存) |
| GET | /api/orders/{id} | 订单详情 |
| GET | /api/orders/user/{userId} | 用户订单 |

## 10. 访问地址

- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/v3/api-docs