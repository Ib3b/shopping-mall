package com.example.shopping.service;

import com.example.shopping.entity.Order;
import com.example.shopping.entity.Product;
import com.example.shopping.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MailServiceTest {
    
    @InjectMocks
    private MailService mailService;
    
    private Order testOrder;
    
    @BeforeEach
    void setUp() {
        User user = new User("testuser", "test@example.com", "password");
        user.setId(1L);
        
        Product product = new Product("测试商品", "描述", new BigDecimal("100"), 100, "分类");
        product.setId(1L);
        
        testOrder = new Order(user, product, 2);
        testOrder.setId(1L);
    }
    
    @Test
    void shouldSendOrderConfirmation() {
        assertDoesNotThrow(() -> mailService.sendOrderConfirmation(testOrder));
    }
    
    @Test
    void shouldSendOrderStatusUpdate() {
        testOrder.setStatus(Order.Status.SHIPPED);
        assertDoesNotThrow(() -> mailService.sendOrderStatusUpdate(testOrder));
    }
}