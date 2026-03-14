package com.example.shopping.service;

import com.example.shopping.common.entity.Order;
import com.example.shopping.common.entity.Product;
import com.example.shopping.common.entity.User;
import com.example.shopping.common.exception.BusinessException;
import com.example.shopping.common.repository.OrderRepository;
import com.example.shopping.common.repository.ProductRepository;
import com.example.shopping.common.repository.UserRepository;
import com.example.shopping.order.service.OrderDataAccessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 订单数据访问服务测试类
 */
@ExtendWith(MockitoExtension.class)
class OrderDataAccessorTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderDataAccessor orderDataAccessor;

    private User testUser;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "password123");
        testUser.setId(1L);

        testProduct = new Product("Test Product", "Description", new BigDecimal("100"), 100, "Category");
        testProduct.setId(1L);

        testOrder = new Order(testUser, testProduct, 2);
        testOrder.setId(1L);
    }

    @Test
    void shouldGetUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User user = orderDataAccessor.getUser(1L);

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> orderDataAccessor.getUser(999L));
    }

    @Test
    void shouldGetProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Product product = orderDataAccessor.getProduct(1L);

        assertNotNull(product);
        assertEquals("Test Product", product.getName());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> orderDataAccessor.getProduct(999L));
    }

    @Test
    void shouldGetOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        Order order = orderDataAccessor.getOrder(1L);

        assertNotNull(order);
        assertEquals(1L, order.getId());
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> orderDataAccessor.getOrder(999L));
    }

    @Test
    void shouldSaveOrder() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        Order saved = orderDataAccessor.saveOrder(testOrder);

        assertNotNull(saved.getId());
    }

    @Test
    void shouldGetOrdersByUserId() {
        when(orderRepository.findUserOrdersOrderByTime(1L)).thenReturn(java.util.List.of(testOrder));

        var orders = orderDataAccessor.getOrdersByUserId(1L);

        assertEquals(1, orders.size());
    }

    @Test
    void shouldGetOrdersByStatus() {
        when(orderRepository.findByStatus(Order.Status.PENDING)).thenReturn(java.util.List.of(testOrder));

        var orders = orderDataAccessor.getOrdersByStatus(Order.Status.PENDING);

        assertEquals(1, orders.size());
    }

    @Test
    void shouldGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(java.util.List.of(testOrder));

        var orders = orderDataAccessor.getAllOrders();

        assertEquals(1, orders.size());
    }

    @Test
    void shouldCountByUserId() {
        when(orderRepository.countByUserId(1L)).thenReturn(5L);

        Long count = orderDataAccessor.countByUserId(1L);

        assertEquals(5L, count);
    }
}