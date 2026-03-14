package com.example.shopping.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA 配置类
 * <p>
 * 启用 JPA 仓库、事务管理和审计功能。
 * 审计功能用于自动填充创建时间和更新时间字段。
 * </p>
 */
@Configuration
@EntityScan(basePackages = "com.example.shopping.common.entity")
@EnableJpaRepositories(basePackages = "com.example.shopping.common.repository")
@EnableTransactionManagement
@EnableJpaAuditing
public class JpaConfig {
}