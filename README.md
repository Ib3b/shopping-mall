# Claude示例开发工程

<p align="center">
  <b>Spring Boot 3.5 多模块示例工程</b>
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
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.11-6DB33F?style=flat-square&logo=springboot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/JDK-21-007396?style=flat-square&logo=openjdk" alt="JDK">
  <img src="https://img.shields.io/badge/License-MIT-blue?style=flat-square" alt="License">
</p>

---

## 项目结构

```
shopping-mall/
├── pom.xml       # 父 POM
├── common/       # 公共模块（实体、DTO、异常、Repository）
├── user/         # 用户模块（用户服务与控制器）
├── product/      # 商品模块（商品服务与控制器）
├── order/        # 订单模块（订单服务、控制器、邮件）
├── mystarter/    # 自定义 Starter 示例
└── app/          # 主应用入口
```

## ✨ 功能特性

| 模块 | 功能 |
|:----:|------|
| 👤 用户 | 注册、查询 |
| 📦 商品 | CRUD、库存管理、缓存 |
| 📑 订单 | 创建订单、自动扣库存、状态流转 |
| 📧 邮件 | 异步模拟发送 |
| 📖 文档 | Swagger UI |
| 🔧 Starter | 自定义 Spring Boot Starter 示例 |

## 🚀 快速开始

```bash
# 构建所有模块
mvn clean package

# 启动应用
cd app
mvn spring-boot:run

# 运行测试
mvn test
```

## 📦 使用 GitHub Packages

```xml
<dependency>
  <groupId>io.github.ib3b</groupId>
  <artifactId>app</artifactId>
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