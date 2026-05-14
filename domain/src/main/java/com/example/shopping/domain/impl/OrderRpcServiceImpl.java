package com.example.shopping.domain.impl;

import com.example.shopping.common.dto.OrderRequest;
import com.example.shopping.common.dto.OrderResponse;
import com.example.shopping.common.entity.Order;
import com.example.shopping.domain.mapper.OrderMapper;
import com.example.shopping.facade.OrderRpcService;
import com.example.shopping.facade.dto.OrderCreateRequest;
import com.example.shopping.facade.dto.OrderDTO;
import com.example.shopping.facade.dto.PageDTO;
import com.example.shopping.facade.enums.OrderStatus;
import com.example.shopping.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单 RPC 服务实现
 * <p>
 * 实现 {@link OrderRpcService} 接口，提供订单相关的 RPC 服务。
 * 作为 facade 层接口与 domain 层服务之间的适配器，负责 DTO 转换和状态枚举映射。
 * </p>
 */
@Service
public class OrderRpcServiceImpl implements OrderRpcService {

    private static final Logger logger = LoggerFactory.getLogger(OrderRpcServiceImpl.class);

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderRpcServiceImpl(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderDTO createOrder(OrderCreateRequest request) {
        logger.info("[RPC] createOrder - userId: {}, productId: {}", request.userId(), request.productId());
        OrderRequest orderRequest = new OrderRequest(request.userId(), request.productId(), request.quantity());
        OrderResponse response = orderService.createOrder(orderRequest);
        return orderMapper.toDTO(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderDTO getOrderById(Long id) {
        logger.info("[RPC] getOrderById - id: {}", id);
        OrderResponse response = orderService.getOrderById(id);
        return orderMapper.toDTO(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OrderDTO> getAllOrders() {
        logger.info("[RPC] getAllOrders");
        return orderService.getAllOrders().stream()
            .map(orderMapper::toDTO)
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageDTO<OrderDTO> getAllOrders(int pageNumber, int pageSize) {
        logger.info("[RPC] getAllOrders (paged) - page: {}, size: {}", pageNumber, pageSize);
        Page<OrderResponse> page = orderService.getAllOrders(PageRequest.of(pageNumber, pageSize));
        List<OrderDTO> content = page.getContent().stream()
            .map(orderMapper::toDTO)
            .toList();
        return new PageDTO<>(
            content,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        logger.info("[RPC] getOrdersByUserId - userId: {}", userId);
        return orderService.getOrdersByUserId(userId).stream()
            .map(orderMapper::toDTO)
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OrderDTO> getOrdersByStatus(OrderStatus status) {
        logger.info("[RPC] getOrdersByStatus - status: {}", status);
        return orderService.getOrdersByStatus(toEntityStatus(status)).stream()
            .map(orderMapper::toDTO)
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        logger.info("[RPC] updateOrderStatus - orderId: {}, newStatus: {}", orderId, newStatus);
        OrderResponse response = orderService.updateOrderStatus(orderId, toEntityStatus(newStatus));
        return orderMapper.toDTO(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelOrder(Long orderId) {
        logger.info("[RPC] cancelOrder - orderId: {}", orderId);
        orderService.cancelOrder(orderId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getUserOrderCount(Long userId) {
        logger.info("[RPC] getUserOrderCount - userId: {}", userId);
        return orderService.getUserOrderCount(userId);
    }

    /**
     * 将 facade 层状态枚举转换为领域层实体状态枚举
     *
     * @param status facade 层订单状态
     * @return 领域层订单状态
     */
    private Order.Status toEntityStatus(OrderStatus status) {
        return Order.Status.valueOf(status.name());
    }
}