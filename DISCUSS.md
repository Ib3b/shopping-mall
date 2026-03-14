# 项目讨论记录

本文档记录了项目构建过程中遇到的问题、解决方案、重构历史以及设计讨论。

---

## 一、重构历史

### 1. 初始架构

项目最初采用多模块分层架构：

```
shopping-mall/
├── common/       # 公共模块（实体、DTO、异常、Repository）
├── facade/       # RPC 服务接口层（二方包）
├── domain/       # 业务领域层（实现 facade 接口）
├── user/         # 用户服务模块
├── product/      # 商品服务模块
├── order/        # 订单服务模块
├── mystarter/    # 自定义 Starter
├── web/          # HTTP 接口层
└── app/          # 主应用入口
```

### 2. 模块合并重构

**背景**：模块过于分散，common/user/product/order 各自独立，增加了维护成本。

**决策**：将 common、user、product、order 合并到 domain 模块。

**重构后结构**：

```
shopping-mall/
├── facade/       # RPC 接口层（无依赖，二方包）
├── domain/       # 业务领域层（聚合所有业务代码）
│   └── src/main/java/com/example/shopping/
│       ├── common/       # Entity、Repository、DTO、异常
│       ├── user/         # 用户服务
│       ├── product/      # 商品服务
│       ├── order/        # 订单服务、邮件服务
│       └── domain/       # RpcService 实现
├── starter/      # 自定义 Spring Boot Starter
├── web/          # HTTP 接口层（Controller）
└── app/          # 主应用入口
```

**依赖关系**：

```
facade (无依赖)      starter (无项目依赖)
     │                    │
     ▼                    │
  domain ◄────────────────┘
     │
     ▼
    web
     │
     ▼
    app
```

### 3. 模块重命名

`mystarter` → `starter`，更符合命名规范。

---

## 二、遇到的问题与解决方案

### 问题 1：Repository 测试找不到配置类

**现象**：合并模块后，Repository 测试报错：
```
IllegalStateException: Unable to find a @SpringBootConfiguration
```

**原因**：测试类使用 `@DataJpaTest`，但 domain 模块没有测试配置类。

**解决方案**：在 domain 模块添加测试配置：

```java
// domain/src/test/java/com/example/shopping/TestApplication.java
@SpringBootApplication
public class TestApplication {
}
```

同时复制 `application-test.yml` 到 domain 的 test resources。

### 问题 2：springdoc-openapi 版本不兼容

**现象**：Release 包启动后访问 API 报错：
```
jakarta.servlet.ServletException: Handler dispatch failed:
java.lang.NoSuchMethodError: 'void org.springframework.web.method.ControllerAdviceBean.<init>(java.lang.Object)'
```

**原因**：`springdoc-openapi 2.3.0` 不兼容 Spring Boot 3.5.x（使用 Spring Framework 6.2.x）。

**解决方案**：升级 springdoc 版本：

```xml
<springdoc.version>2.8.6</springdoc.version>
```

**经验教训**：本地 `mvn spring-boot:run` 可能因为 Maven 缓存表现不同，Release 构建是干净环境，更能暴露真实的依赖问题。

### 问题 3：Git Tag 未更新

**现象**：Release workflow 未触发或发布旧代码。

**原因**：Tag 指向旧的 commit，未更新到最新代码。

**解决方案**：

```bash
git tag -d v1.0.0
git tag v1.0.0
git push origin :refs/tags/v1.0.0
git push origin v1.0.0
```

### 问题 4：LICENSE 文件缺失

**现象**：README 有 MIT License 徽章，但项目无 LICENSE 文件。

**解决方案**：在项目根目录添加标准 MIT LICENSE 文件。

---

## 三、设计讨论

### 1. infrastructure 模块的必要性

**讨论背景**：传统 DDD 架构通常有 infrastructure 模块，本项目是否需要？

**观点**：infrastructure 模块是一种**妥协设计**。

- 理想情况：领域层纯粹，不依赖技术细节
- 现实情况：持久化、外部服务调用需要技术实现

**结论**：

| 情况 | 建议 |
|------|------|
| 只用 JPA | 不需要 infrastructure 模块 |
| 有外部依赖（1-2个） | 在 domain 内加 infrastructure 包 |
| 外部依赖多、复杂 | 独立 infrastructure 模块 |

本项目使用 JPA，Repository 由框架自动实现，无需 infrastructure 模块。未来接入外部服务（RPC、消息队列、通知）时再加。

### 2. JPA 自定义查询方式

**方法命名约定**：Spring Data JPA 根据方法名自动生成查询。

```java
// 自动生成 WHERE username = ?
Optional<User> findByUsername(String username);

// 自动生成 WHERE email = ?
boolean existsByEmail(String email);

// 自动生成 WHERE name LIKE '%keyword%'
List<User> findByNameContaining(String keyword);
```

**常用关键字**：`And`、`Or`、`Between`、`LessThan`、`GreaterThan`、`Like`、`Containing`、`In`、`OrderBy`、`Not`、`IgnoreCase`。

**复杂查询方案**：

1. `@Query` 注解（JPQL/原生 SQL）
2. 自定义 Repository 实现（`UserRepositoryCustom` + `UserRepositoryImpl`）
3. Specification（动态查询）
4. Projection（部分字段查询）

### 3. 防腐层设计

**原则**：领域层不直接依赖外部技术，通过接口隔离。

```java
// ❌ 不好的做法
@Service
public class OrderService {
    private final AliyunSmsClient smsClient;  // 直接依赖

    public void createOrder() {
        smsClient.send(...);
    }
}

// ✅ 好的做法
@Service
public class OrderService {
    private final NotificationGateway gateway;  // 依赖抽象

    public void createOrder() {
        gateway.send(...);
    }
}
```

外部依赖变更时，只需替换实现，业务逻辑不受影响。

### 4. 模块依赖方向

**原则**：依赖从上层指向底层，底层模块不依赖上层。

```
facade (底层，无依赖) ← domain (实现) ← web (调用) ← app (入口)
starter (底层，无项目依赖)
```

facade 作为二方包，可以独立发布给其他服务依赖。

---

## 四、Co-Author 记录

本项目使用 `Co-Authored-By` 记录 AI 辅助编程的贡献：

```
Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>
```

GitHub 会解析此信息并显示在 Contributors 列表中。这符合软件工程最佳实践，清晰记录所有贡献者。

---

## 五、总结

1. **保持简单**：不过度设计，按需添加模块
2. **依赖隔离**：通过接口隔离外部依赖，保护领域层纯粹
3. **版本兼容**：注意 Spring Boot 版本与第三方库的兼容性
4. **规范先行**：LICENSE、README、文档保持完整规范

---

*Document generated on 2026-03-15*