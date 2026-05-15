# Gatling 写入接口压力测试方案

> 基于 2026-05-15 代码审查，现有 Simulation 仅覆盖 3/9 写入端点，需补充。
> 设计目标：在 SQLite 表级锁约束下，系统性地压测所有写入接口。

---

## 一、测试范围

### 写入端点覆盖

| 端点 | 方法 | 当前 | 方案 |
|------|:----:|:----:|:----:|
| `POST /api/users` | createUser | ✅ 已有 | 增强数据生成 |
| `PUT /api/users/{id}` | updateUser | ❌ 缺失 | **新增场景 C** |
| `DELETE /api/users/{id}` | deleteUser | ❌ 缺失 | 低优先级（管理操作） |
| `POST /api/products` | createProduct | ❌ 缺失 | **新增场景 A** |
| `PUT /api/products/{id}` | updateProduct | ❌ 缺失 | **新增场景 A** |
| `DELETE /api/products/{id}` | deleteProduct | ❌ 缺失 | **新增场景 A** |
| `POST /api/orders` | createOrder | ✅ 已有 | 增强数据源 |
| `PUT /api/orders/{id}/status` | updateOrderStatus | ❌ 缺失 | **新增场景 B** |
| `POST /api/orders/{id}/cancel` | cancelOrder | ❌ 缺失 | **新增场景 B** |

### 不纳入本次范围

| 端点 | 原因 |
|------|------|
| `DELETE /api/users/{id}` | 用户删除是管理后台操作，非用户端频率，且删除后影响其他场景的数据依赖 |
| `POST /api/products` + `DELETE` (独立) | Product CRUD 场景中已包含，不单独做 |

---

## 二、场景设计

### 保留现有场景（微调）

#### 场景 1: Browse Products (1000 users, 纯读)
维持不变。每个用户重复 3 次商品列表查询 + 1 次商品详情。

#### 场景 2: Query Orders (500 users, 纯读)
维持不变。每个用户重复 5 次用户订单查询 + 1 次全量查询。

#### 场景 3: Register User (200 users, 写入)
**改动**：使用 `csv feeder` 替代 `AtomicLong` 生成唯一用户名，使数据更可控。每个用户注册后保存 userId 供其他场景使用。

#### 场景 4: Create Order (100 users, 写入)
**改动**：使用 `data.sql` 中已有的用户 ID（1~20）和商品 ID（1~50），避免场景间依赖。保留 `500-1500ms` 间隔避免 SQLite 写锁争用。

### 新增场景

#### 场景 A: Product CRUD (50 users, 写密集)

```
[POST /api/products]  →  201 created
        ↓
[GET /api/products/{id}]  →  200 (缓存命中验证 @CachePut)
        ↓
[PUT /api/products/{id}]  →  200 (更新名称/价格)
        ↓
[GET /api/products/{id}]  →  200 (验证更新后数据)
        ↓
[DELETE /api/products/{id}]  →  204
```

- **用户数**：50（低并发，因为涉及多步骤链式操作）
- **间隔**：500-1000ms
- **Feeder**：每次迭代使用唯一商品名 `"gatling-test-{timestamp}-{n}"`，分类 `"perf-test"`
- **验证点**：
  - 创建返回 201
  - 更新后 `GET` 返回更新后的名称和价格
  - 删除返回 204，再次 GET 返回 400

#### 场景 B: Order Lifecycle (50 users, 写密集)

```
[POST /api/orders]  →  201 (userId=1, productId=1~10, quantity=1)
        ↓
[PUT /api/orders/{id}/status?status=PAID]  →  200
        ↓
[PUT /api/orders/{id}/status?status=SHIPPED]  →  200
        ↓
[PUT /api/orders/{id}/status?status=DELIVERED]  →  200
        ↓
[POST /api/orders/{id}/cancel]  →  400 (终态不可取消)
```

- **用户数**：50
- **间隔**：300-800ms
- **数据源**：使用已知存在的用户 ID（1~5）和商品 ID（1~10）
- **验证点**：
  - 每次状态变更返回 200，状态码符合预期
  - 终态取消返回 400（BusinessException）
  - 全链路 PENDING → PAID → SHIPPED → DELIVERED 通过

#### 场景 C: User Profile Update (100 users, 写入)

```
[POST /api/users]  →  201 (创建带唯一时间戳的用户)
        ↓
[PUT /api/users/{id}]  →  200 (更新用户名/邮箱)
        ↓
[GET /api/users/{id}]  →  200 (验证缓存中为更新后的数据)
```

- **用户数**：100
- **间隔**：200-500ms
- **数据**：每次创建新用户，使用唯一名
- **验证点**：PUT 后 GET 返回更新后的字段

### 增强场景: Mixed Workflow (200 users, 混合)

**改动**：在读写混合工作流结尾增加一次 `POST /api/orders/{id}/cancel` 或 `POST /api/orders` 操作，使混合场景更接近真实用户行为。

---

## 三、并发模型与 SQLite 约束

### 注入策略

```scala
setUp(
  browseProducts.inject(rampUsers(1000).during(30.seconds)),
  queryOrders.inject(rampUsers(500).during(30.seconds)),
  registerUser.inject(rampUsers(200).during(30.seconds)),
  userProfile.inject(rampUsers(100).during(20.seconds)),       // 新增
  createOrder.inject(rampUsers(100).during(30.seconds)),
  mixedWorkflow.inject(rampUsers(200).during(30.seconds)),
  productCrud.inject(rampUsers(50).during(20.seconds)),         // 新增
  orderLifecycle.inject(rampUsers(50).during(20.seconds))       // 新增
)
```

所有场景在 `maxDuration(120.seconds)` 内完成。

### 写入隔离策略

SQLite 使用表级锁，写入时阻塞所有其他操作。为减少锁争用：

1. **写场景并发度低**：Product CRUD (50)、Order Lifecycle (50)、User Profile (100)
2. **写间隔大**：至少 200ms 间隔，Create Order 500-1500ms
3. **高并发写场景错峰**：Product CRUD 和 Order Lifecycle 使用较短的 rampUp 时间，错开执行高峰期
4. **失败容忍**：写场景单独断言（成功 90%），不与读场景混用断言

---

## 四、断言策略

### 场景级断言

| 场景 | 成功率 | 95% 响应时间 | 属性 |
|:----:|:------:|:------------:|:----:|
| Browse Products | > 99% | < 2000ms | 纯读 |
| Query Orders | > 99% | < 2000ms | 纯读 |
| Mixed Workflow | > 95% | < 2500ms | 混合 |
| Register User | > 95% | < 2500ms | 写入 |
| User Profile | > 90% | < 3000ms | 写入 |
| Create Order | > 90% | < 3000ms | 写入 + 乐观锁 |
| Product CRUD | > 90% | < 3000ms | 写入密集 |
| Order Lifecycle | > 90% | < 3000ms | 写入密集 |

### 全局断言

```scala
global.responseTime.percentile3.lt(3000),    // 95% < 3000ms
global.successfulRequests.percent.gt(90),    // 总体成功率 > 90%
global.responseTime.max.lt(15000)            // 最大 < 15000ms
```

---

## 五、数据管理

| 数据 | 来源 | 生命周期 |
|------|------|----------|
| 用户 ID (1-20) | `data.sql` 固定数据 | 始终可用 |
| 商品 ID (1-50) | `data.sql` 固定数据 | 始终可用 |
| 场景 A 创建的商品 | 测试执行时创建，分类 `perf-test` | 测试结束时删除 |
| 场景 C 创建的用户 | 测试执行时创建 | 测试结束时保留（不影响） |

Product CRUD 场景使用 `category: "perf-test"` 标记。测试报告的 `.assertions` 中不依赖特定数据状态。

---

## 六、测试验证

1. **编译验证**：`mvn compile gatling:test -pl app` 正常编译
2. **运行验证**：启动应用后 `mvn gatling:test -pl app` 执行通过
3. **报告检查**：Gatling HTML 报告中每个场景有请求分布、响应时间、成功率数据
4. **写入覆盖检查**：`grep` 确认所有 9 个写入端点至少在 1 个场景中出现

---

## 七、文件变更清单

| 文件 | 变更 |
|------|------|
| `app/src/test/scala/.../ShoppingMallSimulation.scala` | 重写，新增 3 场景 + 增强 2 场景 |

---

*Design approved on 2026-05-15*
