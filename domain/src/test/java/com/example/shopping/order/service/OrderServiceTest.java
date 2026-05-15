package com.example.shopping.order.service;

import com.example.shopping.common.dto.OrderRequest;
import com.example.shopping.common.dto.OrderResponse;
import com.example.shopping.common.entity.Order;
import com.example.shopping.common.entity.Product;
import com.example.shopping.common.entity.User;
import com.example.shopping.common.exception.BusinessException;
import com.example.shopping.order.event.OrderCreatedEvent;
import com.example.shopping.order.event.OrderStatusChangedEvent;
import com.example.shopping.order.state.OrderStateHandlerRegistry;
import com.example.shopping.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private OrderStateHandlerRegistry stateHandlerRegistry;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock(lenient = true)
    private PlatformTransactionManager transactionManager;

    private TransactionStatus transactionStatus;
    private OrderService orderService;

    private User testUser;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        transactionStatus = mock(TransactionStatus.class);
        lenient().when(transactionManager.getTransaction(any(TransactionDefinition.class)))
            .thenReturn(transactionStatus);
        orderService = new OrderService(orderDataAccessor, productService, stateHandlerRegistry,
            eventPublisher, transactionManager);

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

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(2, response.quantity());
        verify(productService).updateStock(1L, 2);
        verify(eventPublisher).publishEvent(any(OrderCreatedEvent.class));
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
        when(stateHandlerRegistry.canTransition(Order.Status.PENDING, Order.Status.PAID)).thenReturn(true);

        OrderResponse response = orderService.updateOrderStatus(1L, Order.Status.PAID);

        assertNotNull(response);
        assertEquals("PAID", response.status());
        verify(eventPublisher).publishEvent(any(OrderStatusChangedEvent.class));
    }

    @Test
    void shouldThrowExceptionForInvalidStatusTransition() {
        when(orderDataAccessor.getOrder(1L)).thenReturn(testOrder);
        when(stateHandlerRegistry.canTransition(Order.Status.PENDING, Order.Status.SHIPPED)).thenReturn(false);

        // 尝试从 PENDING 直接变成 SHIPPED（不合法）
        assertThrows(BusinessException.class, () -> orderService.updateOrderStatus(1L, Order.Status.SHIPPED));
    }

    @Test
    void shouldCancelOrderAndRestoreStock() {
        when(orderDataAccessor.getOrder(1L)).thenReturn(testOrder);
        when(orderDataAccessor.saveOrder(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stateHandlerRegistry.canTransition(Order.Status.PENDING, Order.Status.CANCELLED)).thenReturn(true);

        orderService.cancelOrder(1L);

        verify(productService).updateStock(1L, -2);
    }

    @Test
    void shouldRetryOnOptimisticLockAndSucceed() {
        OrderRequest request = new OrderRequest(1L, 1L, 2);

        when(orderDataAccessor.getUser(1L)).thenReturn(testUser);
        when(orderDataAccessor.getProduct(1L)).thenReturn(testProduct);
        when(orderDataAccessor.saveOrder(any(Order.class)))
            .thenThrow(new OptimisticLockingFailureException("conflict"))
            .thenAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                order.setId(1L);
                return order;
            });

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        verify(orderDataAccessor, times(2)).getUser(1L);
        verify(orderDataAccessor, times(2)).saveOrder(any(Order.class));
        verify(productService).updateStock(1L, 2);
        verify(eventPublisher).publishEvent(any(OrderCreatedEvent.class));
    }

    @Test
    void shouldThrowBusinessExceptionAfterMaxRetries() {
        OrderRequest request = new OrderRequest(1L, 1L, 2);

        when(orderDataAccessor.getUser(1L)).thenReturn(testUser);
        when(orderDataAccessor.getProduct(1L)).thenReturn(testProduct);
        when(orderDataAccessor.saveOrder(any(Order.class)))
            .thenThrow(new OptimisticLockingFailureException("conflict"));

        BusinessException ex = assertThrows(BusinessException.class,
            () -> orderService.createOrder(request));
        assertEquals("系统繁忙，请稍后重试", ex.getMessage());
        verify(productService, times(0)).updateStock(anyLong(), anyInt());
    }

    @Test
    void shouldGetUserOrderCount() {
        when(orderDataAccessor.countByUserId(1L)).thenReturn(5L);

        Long count = orderService.getUserOrderCount(1L);

        assertEquals(5L, count);
    }
}