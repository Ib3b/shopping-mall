# 项目优化计划

> 基于 2026-05-15 全量代码审查发现的 10 个问题，按优先级分三批执行。
> 状态：✅ 全部完成 (2026-05-15)

---

## ✅ 第一批：Bug 修复（高优先级）

### 1. HikariCP minimum-idle 配置 ✅

修复 `application.yml` 中 `minimum-idle: 5` → `1`（与 `maximum-pool-size: 3` 匹配）。

**文件**：`app/src/main/resources/application.yml`

### 2. Starter 自动配置 ✅ 已验证已正确配置

`META-INF/spring/AutoConfiguration.imports` 文件已存在并正确注册。非 Bug，从计划中移除。

### 3. Seed 数据密码加密 ✅

新增 `DataInitializer.java`（`CommandLineRunner` + `@Transactional`），启动时扫描所有用户，自动将明文密码替换为 BCrypt 加密。使用原生 SQL 先初始化 version 字段再通过脏检查更新密码。

**文件**：新增 `app/.../config/DataInitializer.java`

---

## ✅ 第二批：测试与质量

### 4. 删除重复测试 ✅

删除 `app/src/test/java/.../service/` 下 4 个与 `domain` 模块完全重复的测试文件。

### 5. 乐观锁重试测试 ✅

新增 2 个测试：
- `shouldRetryOnOptimisticLockAndSucceed` — 验证第一次 OLE 重试后成功
- `shouldThrowBusinessExceptionAfterMaxRetries` — 验证 3 次重试耗尽后抛出 `BusinessException("系统繁忙，请稍后重试")`

**文件**：`domain/src/test/java/.../order/service/OrderServiceTest.java`

---

## ✅ 第三批：架构治理

### 6/7. 缓存策略统一为方案 A ✅

按方案 A（`@CachePut` 精更）重构所有缓存注解：

| 方法 | 改动前 | 改动后 |
|------|--------|--------|
| `ProductService.createProduct()` | `@CacheEvict(allEntries = true)` | 移除（新建 ID 不影响已有缓存） |
| `ProductService.updateProduct()` | `@CacheEvict(allEntries = true)` | `@CachePut(key = "#id")` |
| `ProductService.deleteProduct()` | `@CacheEvict(allEntries = true)` | `@CacheEvict(key = "#id")` |
| `UserService.createUser()` | `@CacheEvict(allEntries = true)` | 移除（新建 ID 不影响已有缓存） |
| `UserService.deleteUser()` | `@CacheEvict(allEntries = true)` | `@CacheEvict(key = "#id")` |
| `UserService.updateUser()` | `@CacheEvict(key = "#id")` | 不变 ✅ |

### 8. 缓存配置去重 ✅

移除 `application.yml` 中 caffeine spec 行（dead code），统一由 `CacheConfig.java` 管理。

### 9. orders 表添加 product_id 索引 ✅

在 `schema.sql` 中添加 `CREATE INDEX IF NOT EXISTS idx_order_product ON orders(product_id)`。

### 10. 商品搜索性能（信息留档）

`LIKE %:keyword%` 全表扫描。当前 50 条记录不构成实际影响，留档作为未来优化参考。

---

## 执行总结

```
✅ 第一批（Bug 修复）
  ├── 1. HikariCP minimum-idle        → application.yml
  ├── 2. Starter 自动配置              → 已验证 OK
  └── 3. Seed 数据密码加密             → DataInitializer.java

✅ 第二批（测试与质量）
  ├── 4. 删除重复测试                  → -4 files
  └── 5. 乐观锁重试测试                → +2 tests

✅ 第三批（架构治理）
  ├── 6/7. 缓存策略方案 A              → 6 annotations changed
  ├── 8. 缓存配置去重                  → application.yml
  ├── 9. product_id 索引               → schema.sql
  └── 10. 搜索性能留档                 → 文档记录
```
