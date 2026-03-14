package com.example.shopping.repository;

import com.example.shopping.common.entity.Product;
import com.example.shopping.common.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Test
    void shouldSaveAndFindProduct() {
        Product product = new Product(
            "测试商品", "这是一个测试商品", 
            new BigDecimal("99.99"), 100, "测试分类"
        );
        Product saved = productRepository.save(product);
        
        assertNotNull(saved.getId());
        assertEquals("测试商品", saved.getName());
        assertEquals(new BigDecimal("99.99"), saved.getPrice());
        assertEquals(100, saved.getStock());
    }
    
    @Test
    void shouldFindByCategory() {
        productRepository.save(new Product("商品1", "描述1", new BigDecimal("10"), 10, "电子产品"));
        productRepository.save(new Product("商品2", "描述2", new BigDecimal("20"), 20, "电子产品"));
        productRepository.save(new Product("商品3", "描述3", new BigDecimal("30"), 30, "服装"));
        
        List<Product> electronics = productRepository.findByCategory("电子产品");
        assertEquals(2, electronics.size());
        
        List<Product> clothing = productRepository.findByCategory("服装");
        assertEquals(1, clothing.size());
    }
    
    @Test
    void shouldFindInStockProducts() {
        productRepository.save(new Product("有库存", "有", new BigDecimal("10"), 10, "分类1"));
        productRepository.save(new Product("无库存", "无", new BigDecimal("20"), 0, "分类2"));
        
        List<Product> inStock = productRepository.findInStockProducts();
        assertTrue(inStock.stream().allMatch(p -> p.getStock() > 0));
        assertEquals(1, inStock.size());
    }
    
    @Test
    void shouldSearchByKeyword() {
        productRepository.save(new Product("iPhone15", "苹果手机", new BigDecimal("8999"), 50, "电子产品"));
        productRepository.save(new Product("小米手机", "小米旗舰", new BigDecimal("3999"), 30, "电子产品"));
        
        List<Product> results = productRepository.searchByKeyword("手机");
        assertEquals(2, results.size());
    }
}