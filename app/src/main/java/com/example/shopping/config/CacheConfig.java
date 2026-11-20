package com.example.shopping.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存配置类
 * <p>
 * 配置 Caffeine 缓存管理器，用于缓存用户和商品数据。
 * 通过 {@link TransactionAwareCacheManagerProxy} 包装后，{@code @CachePut}
 * 与 {@code @CacheEvict} 只在事务提交后生效；事务回滚时缓存保持不变，
 * 避免数据库与缓存不一致。
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
     *   <li>缓存写操作与事务提交绑定</li>
     * </ul>
     * </p>
     *
     * @return 缓存管理器
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager target = new CaffeineCacheManager();
        target.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats());
        target.setCacheNames(List.of("userCache", "productCache"));

        TransactionAwareCacheManagerProxy proxy = new TransactionAwareCacheManagerProxy(target);
        proxy.afterPropertiesSet();
        return proxy;
    }
}
