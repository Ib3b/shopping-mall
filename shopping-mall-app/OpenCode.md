# OpenCode 项目记忆文件

## 项目信息

- **项目名称**: shopping-mall (网购商城示例工程)
- **路径**: C:\Users\PC\Documents\opencode-test\shopping-mall
- **技术栈**: Spring Boot 3.2.5 + JDK 21 + SQLite + HikariCP + Caffeine + JMeter

## 项目要求 (来自Prd.md)

1. 使用JDK 21特性 (Record、Switch增强、Sequenced Collections)，但禁止使用虚拟线程
2. 使用传统ThreadPoolExecutor线程池
3. 使用HikariCP连接池连接SQLite
4. 使用Spring Cache + Caffeine本地缓存
5. 包含用户、商品、订单管理功能
6. 订单创建时自动扣库存
7. 异步发送邮件通知 (模拟，控制台输出)
8. Swagger UI接入
9. 100条示例数据 (User 20 + Product 50 + Order 30)
10. 完整单元测试

## 已知问题与解决方案

### 1. 测试编译错误 - JSONAssertion
- **问题**: JMeter测试计划中使用了JSONAssertion导致报错
- **解决**: 移除JSONAssertion组件，只保留ResponseAssertion

### 2. 测试编译错误 - cancelOrder返回值
- **问题**: OrderControllerTest中cancelOrder返回void但测试期望返回OrderResponse
- **解决**: 改为mock doNothing() 和 getOrderById()

### 3. 单元测试Mockito兼容性问题 (JDK 25)
- **问题**: JDK 25下Mockito无法mock Spring Boot管理的Service类
- **现象**: "Could not modify all classes" 错误
- **状态**: 未完全解决，但应用可正常运行

### 4. 端口占用
- **问题**: 8080端口被占用导致启动失败
- **解决**: 使用 taskkill //F //PID <进程ID> 关闭占用进程

### 5. SQLite Hibernate方言
- **问题**: 需要使用Hibernate社区版的SQLiteDialect
- **解决**: 添加 hibernate-community-dialects 依赖，配置 `org.hibernate.community.dialect.SQLiteDialect`

## 常用命令

### 启动应用
```bash
cd C:\Users\PC\Documents\opencode-test\shopping-mall
mvn spring-boot:run
```

### 关闭占用端口
```bash
netstat -ano | findstr :8080
taskkill //F //PID <PID>
```

### 运行性能测试
```bash
cd C:\Users\PC\Documents\opencode-test\shopping-mall
jmeter -n -t jmeter/shopping-mall-test-plan.jmx -l results.jtl -e -o html-report
```

### 编译项目
```bash
mvn clean compile
```

## 关键文件位置

| 文件 | 路径 |
|------|------|
| 主配置 | application.yml |
| 示例数据 | data.sql |
| 测试计划 | jmeter/shopping-mall-test-plan.jmx |
| 性能报告 | html-report/index.html |
| 项目需求 | Prd.md |

## API基础URL
- 本地: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html

## 性能测试结果 (2026-03-09)
- 总请求数: 6015
- 吞吐量: 202.3 req/s
- 平均响应时间: 18ms
- 错误率: 0.25%

## 后续修改注意事项

1. 修改Prd.md后需同步更新相关代码
2. 测试文件在JDK 25下有兼容性问题，如需修复考虑升级Mockito版本
3. 数据库文件 shopping.db 会自动创建在项目根目录
4. JMeter版本5.6.3对中文支持有问题，避免在测试计划中使用中文注释
5. SQLite是嵌入式数据库，引擎已嵌入sqlite-jdbc JAR中，无需安装独立数据库服务器

## SQLite技术说明

SQLite vs MySQL:
- MySQL: 客户端-服务器架构，需要安装MySQL Server
- SQLite: 嵌入式/文件型，引擎在sqlite-jdbc JAR中，无需安装

依赖: org.xerial/sqlite-jdbc/3.45.1.0

## Arthas使用问题

### 问题描述
- JDK 25下Arthas 4.1.8的telnet连接失败: "Connection refused"
- 可能原因: JDK 25的新安全特性导致端口绑定问题

### 替代方案
已添加请求日志拦截器，启动应用后会在控制台显示API调用路径:
```
>>> GET /api/products/1
<<< GET /api/products/1 - Status: 200
```

拦截器位置: `src/main/java/com/example/shopping/interceptor/RequestLoggingInterceptor.java`

### Arthas正确使用方式
1. 启动应用: `mvn spring-boot:run`
2. 启动Arthas: `java -jar arthas-boot.jar <PID>`
3. 在Arthas界面执行命令:
   - `stack com.example.shopping.service.ProductService getProductById` - 查看调用栈
   - `trace com.example.shopping.service.ProductService getProductById` - 追踪耗时
   - `watch com.example.shopping.service.ProductService getProductById "{params,returnObj}"` - 监视参数返回值
4. 另开终端发送请求触发调用
5. 退出: `quit`