package com.example.shopping.order.controller;

import com.example.shopping.common.dto.OrderRequest;
import com.example.shopping.common.dto.OrderResponse;
import com.example.shopping.common.entity.Order;
import com.example.shopping.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 * <p>
 * 提供订单的创建、查询、状态管理等 REST API 接口。
 * </p>
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "订单管理", description = "订单创建、查询、状态管理接口")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单
     *
     * @param request 订单请求
     * @return 创建的订单
     */
    @PostMapping
    @Operation(summary = "创建订单", description = "创建新订单并扣减库存")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 根据ID获取订单
     *
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取订单", description = "根据ID获取订单详情")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 分页获取所有订单
     *
     * @param pageable 分页参数（默认每页10条）
     * @return 订单分页列表
     */
    @GetMapping
    @Operation(summary = "获取所有订单", description = "分页获取订单列表")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<OrderResponse> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * 根据用户ID获取订单
     *
     * @param userId 用户ID
     * @return 用户订单列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户订单", description = "获取指定用户的订单列表")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable Long userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * 根据状态获取订单
     *
     * @param status 订单状态
     * @return 订单列表
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "根据状态查询", description = "获取指定状态的订单列表")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable Order.Status status) {
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    /**
     * 更新订单状态
     *
     * @param id     订单ID
     * @param status 新状态
     * @return 更新后的订单
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新订单状态", description = "更新订单状态")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.Status status) {
        OrderResponse response = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(response);
    }

    /**
     * 取消订单
     *
     * @param id 订单ID
     * @return 取消后的订单
     */
    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消订单", description = "取消订单并恢复库存")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户订单数量
     *
     * @param userId 用户ID
     * @return 订单数量
     */
    @GetMapping("/user/{userId}/count")
    @Operation(summary = "用户订单数量", description = "获取用户订单数量")
    public ResponseEntity<Long> getUserOrderCount(@PathVariable Long userId) {
        Long count = orderService.getUserOrderCount(userId);
        return ResponseEntity.ok(count);
    }
}