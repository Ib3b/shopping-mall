package com.example.shopping.config;

import com.example.shopping.common.dto.ProductResponse;
import com.example.shopping.common.entity.Product;
import com.example.shopping.common.repository.ProductRepository;
import com.example.shopping.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 验证缓存写操作与事务边界一致。
 */
@SpringBootTest
@ActiveProfiles("test")
class CacheTransactionTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @BeforeEach
    void clearCache() {
        Cache cache = cacheManager.getCache("productCache");
        assertNotNull(cache);
        cache.clear();
    }

    @Test
    void shouldKeepCacheUnchangedWhenStockUpdateRollsBack() {
        Product product = productRepository.save(new Product(
            "cache-rollback", "cache test", new BigDecimal("10.00"), 10, "test"));
        Long productId = product.getId();

        // 预热缓存：stock=10
        assertEquals(10, productService.getProductById(productId).stock());

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.executeWithoutResult(status -> {
            productService.updateStock(productId, 4);
            status.setRollbackOnly();
        });

        // 数据库已回滚
        assertEquals(10, productRepository.findById(productId).orElseThrow().getStock());

        // 缓存也必须保持回滚前的值，而不是 6
        ProductResponse cached = cacheManager.getCache("productCache")
            .get(productId, ProductResponse.class);
        assertNotNull(cached);
        assertEquals(10, cached.stock());
    }
}
