package com.example.shopping.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置类
 * <p>
 * 配置 Caffeine 缓存管理器，用于缓存用户和商品数据。
 * </p>
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 创建缓存管理器
     * <p>
     * 配置：
     * <ul>
     *   <li>最大缓存条目: 1000</li>
     *   <li>写入后过期时间: 5分钟</li>
     *   <li>启用统计信息</li>
     * </ul>
     * </p>
     *
     * @return 缓存管理器
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats());
        cacheManager.setCacheNames(java.util.List.of("userCache", "productCache"));
        return cacheManager;
    }
}