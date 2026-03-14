# 🛒 Shopping Mall

<p align="center">
  <b>Spring Boot 3.2 网购商城 REST API 示例工程</b>
</p>

<p align="center">
  <a href="https://github.com/Ib3b/shopping-mall/actions/workflows/ci.yml">
    <img src="https://img.shields.io/github/actions/workflow/status/Ib3b/shopping-mall/ci.yml?style=flat-square&logo=github&label=CI" alt="CI">
  </a>
  <a href="https://github.com/Ib3b/shopping-mall/packages">
    <img src="https://img.shields.io/badge/GitHub-Packages-8A2BE2?style=flat-square&logo=github" alt="GitHub Packages">
  </a>
  <a href="https://github.com/Ib3b/shopping-mall/releases">
    <img src="https://img.shields.io/github/v/release/Ib3b/shopping-mall?style=flat-square&logo=github" alt="Release">
  </a>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?style=flat-square&logo=springboot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/JDK-21-007396?style=flat-square&logo=openjdk" alt="JDK">
  <img src="https://img.shields.io/badge/License-MIT-blue?style=flat-square" alt="License">
</p>

---

## ✨ 功能特性

| 模块 | 功能 |
|:----:|------|
| 👤 用户 | 注册、查询 |
| 📦 商品 | CRUD、库存管理、缓存 |
| 📑 订单 | 创建订单、自动扣库存、状态流转 |
| 📧 邮件 | 异步模拟发送 |
| 📖 文档 | Swagger UI |

## 🚀 快速开始

```bash
# 启动应用
mvn spring-boot:run

# 运行测试
mvn test
```

## 📦 使用 GitHub Packages

```xml
<dependency>
  <groupId>io.github.ib3b</groupId>
  <artifactId>shopping-mall</artifactId>
  <version>1.0.0</version>
</dependency>
```

## 🔗 访问地址

| 服务 | 地址 |
|------|------|
| API | http://localhost:8080 |
| Swagger | http://localhost:8080/swagger-ui.html |

## 📌 API 端点

<details>
<summary><b>用户管理</b></summary>

```
GET    /api/users           # 获取所有用户
GET    /api/users/{id}      # 获取用户
POST   /api/users           # 创建用户
```
</details>

<details>
<summary><b>商品管理</b></summary>

```
GET    /api/products                    # 获取所有商品
GET    /api/products/{id}               # 获取商品
GET    /api/products/category/{cat}     # 按分类查询
POST   /api/products                    # 创建商品
PUT    /api/products/{id}               # 更新商品
DELETE /api/products/{id}               # 删除商品
```
</details>

<details>
<summary><b>订单管理</b></summary>

```
GET    /api/orders                    # 获取所有订单
GET    /api/orders/{id}               # 获取订单
GET    /api/orders/user/{userId}      # 获取用户订单
POST   /api/orders                    # 创建订单（自动扣库存）
PUT    /api/orders/{id}/status        # 更新订单状态
POST   /api/orders/{id}/cancel        # 取消订单
```
</details>

## 📋 运行要求

- JDK 21+
- Maven 3.6+

---

<p align="center"><i>Made with ❤️ using Spring Boot</i></p>