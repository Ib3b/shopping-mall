package com.example.shopping.domain.impl;

import com.example.shopping.common.dto.OrderRequest;
import com.example.shopping.common.dto.OrderResponse;
import com.example.shopping.common.entity.Order;
import com.example.shopping.facade.OrderRpcService;
import com.example.shopping.facade.dto.OrderCreateRequest;
import com.example.shopping.facade.dto.OrderDTO;
import com.example.shopping.facade.enums.OrderStatus;
import com.example.shopping.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单 RPC 服务实现
 */
@Service
public class OrderRpcServiceImpl implements OrderRpcService {

    private static final Logger logger = LoggerFactory.getLogger(OrderRpcServiceImpl.class);

    private final OrderService orderService;

    public OrderRpcServiceImpl(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public OrderDTO createOrder(OrderCreateRequest request) {
        logger.info("[RPC] createOrder - userId: {}, productId: {}", request.userId(), request.productId());
        OrderRequest orderRequest = new OrderRequest(request.userId(), request.productId(), request.quantity());
        OrderResponse response = orderService.createOrder(orderRequest);
        return toDTO(response);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        logger.info("[RPC] getOrderById - id: {}", id);
        OrderResponse response = orderService.getOrderById(id);
        return toDTO(response);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        logger.info("[RPC] getAllOrders");
        return orderService.getAllOrders().stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        logger.info("[RPC] getOrdersByUserId - userId: {}", userId);
        return orderService.getOrdersByUserId(userId).stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public List<OrderDTO> getOrdersByStatus(OrderStatus status) {
        logger.info("[RPC] getOrdersByStatus - status: {}", status);
        return orderService.getOrdersByStatus(toEntityStatus(status)).stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        logger.info("[RPC] updateOrderStatus - orderId: {}, newStatus: {}", orderId, newStatus);
        OrderResponse response = orderService.updateOrderStatus(orderId, toEntityStatus(newStatus));
        return toDTO(response);
    }

    @Override
    public void cancelOrder(Long orderId) {
        logger.info("[RPC] cancelOrder - orderId: {}", orderId);
        orderService.cancelOrder(orderId);
    }

    @Override
    public Long getUserOrderCount(Long userId) {
        logger.info("[RPC] getUserOrderCount - userId: {}", userId);
        return orderService.getUserOrderCount(userId);
    }

    private OrderDTO toDTO(OrderResponse response) {
        return new OrderDTO(
            response.id(),
            response.userId(),
            response.username(),
            response.productId(),
            response.productName(),
            response.quantity(),
            response.totalPrice(),
            response.status(),
            response.statusDescription(),
            response.createdAt(),
            response.updatedAt()
        );
    }

    private Order.Status toEntityStatus(OrderStatus status) {
        return Order.Status.valueOf(status.name());
    }
}