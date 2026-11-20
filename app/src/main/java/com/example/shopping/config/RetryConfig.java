package com.example.shopping.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.retry.annotation.EnableRetry;

/**
 * 启用方法级重试。
 * <p>
 * 重试切面必须位于事务切面之外：SQLite 在 WAL 模式下可能返回
 * {@code SQLITE_BUSY_SNAPSHOT}，只有重新开启事务才能恢复。
 * </p>
 */
@Configuration
@EnableRetry(order = Ordered.HIGHEST_PRECEDENCE)
public class RetryConfig {
}
