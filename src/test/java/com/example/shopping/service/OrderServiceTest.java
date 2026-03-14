package com.example.shopping.service;

import com.example.shopping.dto.OrderRequest;
import com.example.shopping.dto.OrderResponse;
import com.example.shopping.entity.Order;
import com.example.shopping.entity.Product;
import com.example.shopping.entity.User;
import com.example.shopping.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 订单服务测试类
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderDataAccessor orderDataAccessor;

    @Mock
    private ProductService productService;

    @Mock
    private MailService mailService;

    private OrderService orderService;

    private User testUser;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderDataAccessor, productService, mailService);

        testUser = new User("testuser", "test@example.com", "password123");
        testUser.setId(1L);

        testProduct = new Product("Test Product", "Description", new BigDecimal("100"), 100, "Category");
        testProduct.setId(1L);

        testOrder = new Order(testUser, testProduct, 2);
        testOrder.setId(1L);
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        OrderRequest request = new OrderRequest(1L, 1L, 2);

        when(orderDataAccessor.getUser(1L)).thenReturn(testUser);
        when(orderDataAccessor.getProduct(1L)).thenReturn(testProduct);
        when(orderDataAccessor.saveOrder(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });
        doNothing().when(mailService).sendOrderConfirmation(any(Order.class));

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(2, response.quantity());
        verify(productService).updateStock(1L, 2);
        verify(mailService).sendOrderConfirmation(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnCreateOrder() {
        OrderRequest request = new OrderRequest(999L, 1L, 2);

        when(orderDataAccessor.getUser(999L)).thenThrow(new BusinessException("用户不存在"));

        assertThrows(BusinessException.class, () -> orderService.createOrder(request));
    }

    @Test
    void shouldThrowExceptionWhenProductNotFoundOnCreateOrder() {
        OrderRequest request = new OrderRequest(1L, 999L, 2);

        when(orderDataAccessor.getUser(1L)).thenReturn(testUser);
        when(orderDataAccessor.getProduct(999L)).thenThrow(new BusinessException("商品不存在"));

        assertThrows(BusinessException.class, () -> orderService.createOrder(request));
    }

    @Test
    void shouldThrowExceptionWhenInsufficientStock() {
        OrderRequest request = new OrderRequest(1L, 1L, 200);

        when(orderDataAccessor.getUser(1L)).thenReturn(testUser);
        when(orderDataAccessor.getProduct(1L)).thenReturn(testProduct);

        assertThrows(BusinessException.class, () -> orderService.createOrder(request));
    }

    @Test
    void shouldGetOrderById() {
        when(orderDataAccessor.getOrder(1L)).thenReturn(testOrder);

        OrderResponse response = orderService.getOrderById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        when(orderDataAccessor.getOrder(999L)).thenThrow(new BusinessException("订单不存在"));

        assertThrows(BusinessException.class, () -> orderService.getOrderById(999L));
    }

    @Test
    void shouldGetOrdersByUserId() {
        when(orderDataAccessor.getOrdersByUserId(1L))
            .thenReturn(List.of(testOrder));

        var responses = orderService.getOrdersByUserId(1L);

        assertEquals(1, responses.size());
    }

    @Test
    void shouldGetOrdersByStatus() {
        when(orderDataAccessor.getOrdersByStatus(Order.Status.PENDING))
            .thenReturn(List.of(testOrder));

        var responses = orderService.getOrdersByStatus(Order.Status.PENDING);

        assertEquals(1, responses.size());
    }

    @Test
    void shouldGetAllOrders() {
        when(orderDataAccessor.getAllOrders()).thenReturn(List.of(testOrder));

        var responses = orderService.getAllOrders();

        assertEquals(1, responses.size());
    }

    @Test
    void shouldUpdateOrderStatus() {
        when(orderDataAccessor.getOrder(1L)).thenReturn(testOrder);
        when(orderDataAccessor.saveOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.updateOrderStatus(1L, Order.Status.PAID);

        assertNotNull(response);
        assertEquals("PAID", response.status());
    }

    @Test
    void shouldThrowExceptionForInvalidStatusTransition() {
        when(orderDataAccessor.getOrder(1L)).thenReturn(testOrder);

        // 尝试从 PENDING 直接变成 SHIPPED（不合法）
        assertThrows(BusinessException.class, () -> orderService.updateOrderStatus(1L, Order.Status.SHIPPED));
    }

    @Test
    void shouldCancelOrderAndRestoreStock() {
        when(orderDataAccessor.getOrder(1L)).thenReturn(testOrder);
        when(orderDataAccessor.saveOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.cancelOrder(1L);

        verify(productService).updateStock(1L, -2);
    }

    @Test
    void shouldGetUserOrderCount() {
        when(orderDataAccessor.countByUserId(1L)).thenReturn(5L);

        Long count = orderService.getUserOrderCount(1L);

        assertEquals(5L, count);
    }
}