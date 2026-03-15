package com.example.shopping.controller;

import com.example.shopping.facade.OrderRpcService;
import com.example.shopping.facade.dto.OrderCreateRequest;
import com.example.shopping.facade.dto.OrderDTO;
import com.example.shopping.facade.dto.PageDTO;
import com.example.shopping.facade.enums.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 订单控制器测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderRpcService orderRpcService;

    @Test
    void shouldCreateOrder() throws Exception {
        OrderCreateRequest request = new OrderCreateRequest(1L, 1L, 2);
        OrderDTO response = new OrderDTO(1L, 1L, "testuser", 1L, "Test Product", 2,
            new BigDecimal("200"), "PENDING", "Pending", LocalDateTime.now(), LocalDateTime.now());

        when(orderRpcService.createOrder(any(OrderCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldGetOrderById() throws Exception {
        OrderDTO response = new OrderDTO(1L, 1L, "testuser", 1L, "Test Product", 2,
            new BigDecimal("200"), "PENDING", "Pending", LocalDateTime.now(), LocalDateTime.now());

        when(orderRpcService.getOrderById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/orders/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldGetOrdersByUserId() throws Exception {
        OrderDTO response = new OrderDTO(1L, 1L, "testuser", 1L, "Test Product", 2,
            new BigDecimal("200"), "PENDING", "Pending", LocalDateTime.now(), LocalDateTime.now());

        when(orderRpcService.getOrdersByUserId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/orders/user/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldGetAllOrders() throws Exception {
        OrderDTO order1 = new OrderDTO(1L, 1L, "user1", 1L, "Product 1", 1,
            new BigDecimal("100"), "PENDING", "Pending", LocalDateTime.now(), LocalDateTime.now());
        OrderDTO order2 = new OrderDTO(2L, 2L, "user2", 2L, "Product 2", 2,
            new BigDecimal("200"), "PAID", "Paid", LocalDateTime.now(), LocalDateTime.now());

        PageDTO<OrderDTO> page = new PageDTO<>(List.of(order1, order2), 0, 10, 2, 1, true, true);
        when(orderRpcService.getAllOrders(anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void shouldUpdateOrderStatus() throws Exception {
        OrderDTO response = new OrderDTO(1L, 1L, "testuser", 1L, "Test Product", 2,
            new BigDecimal("200"), "PAID", "Paid", LocalDateTime.now(), LocalDateTime.now());

        when(orderRpcService.updateOrderStatus(1L, OrderStatus.PAID)).thenReturn(response);

        mockMvc.perform(put("/api/orders/1/status")
                .param("status", "PAID"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void shouldCancelOrder() throws Exception {
        OrderDTO response = new OrderDTO(1L, 1L, "testuser", 1L, "Test Product", 2,
            new BigDecimal("200"), "CANCELLED", "Cancelled", LocalDateTime.now(), LocalDateTime.now());

        doNothing().when(orderRpcService).cancelOrder(1L);
        when(orderRpcService.getOrderById(1L)).thenReturn(response);

        mockMvc.perform(post("/api/orders/1/cancel"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void shouldGetUserOrderCount() throws Exception {
        when(orderRpcService.getUserOrderCount(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/orders/user/1/count"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(5));
    }
}