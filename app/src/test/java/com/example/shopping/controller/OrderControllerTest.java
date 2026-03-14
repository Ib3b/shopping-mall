package com.example.shopping.controller;

import com.example.shopping.common.dto.OrderRequest;
import com.example.shopping.common.dto.OrderResponse;
import com.example.shopping.common.entity.Order;
import com.example.shopping.order.controller.OrderController;
import com.example.shopping.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 订单控制器测试类
 */
@WebMvcTest(value = OrderController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class
    })
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @Test
    void shouldCreateOrder() throws Exception {
        OrderRequest request = new OrderRequest(1L, 1L, 2);
        OrderResponse response = new OrderResponse(1L, 1L, "testuser", 1L, "Test Product", 2,
            new BigDecimal("200"), "PENDING", "Pending", LocalDateTime.now(), LocalDateTime.now());

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldGetOrderById() throws Exception {
        OrderResponse response = new OrderResponse(1L, 1L, "testuser", 1L, "Test Product", 2,
            new BigDecimal("200"), "PENDING", "Pending", LocalDateTime.now(), LocalDateTime.now());

        when(orderService.getOrderById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/orders/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldGetOrdersByUserId() throws Exception {
        OrderResponse response = new OrderResponse(1L, 1L, "testuser", 1L, "Test Product", 2,
            new BigDecimal("200"), "PENDING", "Pending", LocalDateTime.now(), LocalDateTime.now());

        when(orderService.getOrdersByUserId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/orders/user/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldGetAllOrders() throws Exception {
        OrderResponse order1 = new OrderResponse(1L, 1L, "user1", 1L, "Product 1", 1,
            new BigDecimal("100"), "PENDING", "Pending", LocalDateTime.now(), LocalDateTime.now());
        OrderResponse order2 = new OrderResponse(2L, 2L, "user2", 2L, "Product 2", 2,
            new BigDecimal("200"), "PAID", "Paid", LocalDateTime.now(), LocalDateTime.now());

        Page<OrderResponse> page = new PageImpl<>(List.of(order1, order2));
        when(orderService.getAllOrders(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void shouldUpdateOrderStatus() throws Exception {
        OrderResponse response = new OrderResponse(1L, 1L, "testuser", 1L, "Test Product", 2,
            new BigDecimal("200"), "PAID", "Paid", LocalDateTime.now(), LocalDateTime.now());

        when(orderService.updateOrderStatus(1L, Order.Status.PAID)).thenReturn(response);

        mockMvc.perform(put("/api/orders/1/status")
                .param("status", "PAID"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void shouldCancelOrder() throws Exception {
        OrderResponse response = new OrderResponse(1L, 1L, "testuser", 1L, "Test Product", 2,
            new BigDecimal("200"), "CANCELLED", "Cancelled", LocalDateTime.now(), LocalDateTime.now());

        doNothing().when(orderService).cancelOrder(1L);
        when(orderService.getOrderById(1L)).thenReturn(response);

        mockMvc.perform(post("/api/orders/1/cancel"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void shouldGetUserOrderCount() throws Exception {
        when(orderService.getUserOrderCount(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/orders/user/1/count"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(5));
    }
}