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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单服务类
 * <p>
 * 提供订单的创建、查询、状态管理等业务逻辑。
 * 使用状态模式管理状态转换，使用观察者模式（事件驱动）处理通知。
 * </p>
 */
@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderDataAccessor orderDataAccessor;
    private final ProductService productService;
    private final OrderStateHandlerRegistry stateHandlerRegistry;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(OrderDataAccessor orderDataAccessor,
                        ProductService productService,
                        OrderStateHandlerRegistry stateHandlerRegistry,
                        ApplicationEventPublisher eventPublisher) {
        this.orderDataAccessor = orderDataAccessor;
        this.productService = productService;
        this.stateHandlerRegistry = stateHandlerRegistry;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 创建订单
     * <p>
     * 创建订单时会自动扣减库存并发布订单创建事件。
     * </p>
     *
     * @param request 订单请求
     * @return 订单响应
     * @throws BusinessException 当用户不存在、商品不存在或库存不足时抛出
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        logger.info("创建订单 - 用户ID: {}, 商品ID: {}, 数量: {}",
            request.userId(), request.productId(), request.quantity());

        User user = orderDataAccessor.getUser(request.userId());
        Product product = orderDataAccessor.getProduct(request.productId());

        if (product.getStock() < request.quantity()) {
            throw new BusinessException("商品库存不足，当前库存: " + product.getStock());
        }

        Order order = new Order(user, product, request.quantity());
        Order saved = orderDataAccessor.saveOrder(order);

        productService.updateStock(request.productId(), request.quantity());

        // 发布订单创建事件（观察者模式）
        eventPublisher.publishEvent(new OrderCreatedEvent(saved));

        logger.info("订单创建成功 - 订单ID: {}", saved.getId());

        return toResponse(saved);
    }

    /**
     * 根据ID获取订单
     *
     * @param id 订单ID
     * @return 订单响应
     * @throws BusinessException 当订单不存在时抛出
     */
    public OrderResponse getOrderById(Long id) {
        Order order = orderDataAccessor.getOrder(id);
        return toResponse(order);
    }

    /**
     * 根据用户ID获取订单列表
     *
     * @param userId 用户ID
     * @return 订单响应列表
     */
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderDataAccessor.getOrdersByUserId(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * 根据状态获取订单列表
     *
     * @param status 订单状态
     * @return 订单响应列表
     */
    public List<OrderResponse> getOrdersByStatus(Order.Status status) {
        return orderDataAccessor.getOrdersByStatus(status).stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * 分页获取所有订单
     *
     * @param pageable 分页参数
     * @return 订单分页响应
     */
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderDataAccessor.getAllOrders(pageable)
            .map(this::toResponse);
    }

    /**
     * 获取所有订单（不分页，用于内部调用）
     *
     * @return 订单响应列表
     */
    public List<OrderResponse> getAllOrders() {
        return orderDataAccessor.getAllOrders().stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * 更新订单状态
     * <p>
     * 使用状态模式验证状态转换的合法性，
     * 转换成功后发布状态变更事件。
     * </p>
     *
     * @param orderId   订单ID
     * @param newStatus 新状态
     * @return 订单响应
     * @throws BusinessException 当订单不存在或状态转换不合法时抛出
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, Order.Status newStatus) {
        Order order = orderDataAccessor.getOrder(orderId);
        Order.Status currentStatus = order.getStatus();

        // 使用状态模式验证状态转换
        if (!stateHandlerRegistry.canTransition(currentStatus, newStatus)) {
            throw new BusinessException("订单状态不允许从 " +
                currentStatus.getDescription() + " 变更为 " +
                newStatus.getDescription());
        }

        // 取消订单时恢复库存
        if (newStatus == Order.Status.CANCELLED) {
            productService.updateStock(order.getProduct().getId(), -order.getQuantity());
        }

        order.setStatus(newStatus);
        Order saved = orderDataAccessor.saveOrder(order);

        // 发布状态变更事件（观察者模式）
        eventPublisher.publishEvent(new OrderStatusChangedEvent(saved, currentStatus, newStatus));

        logger.info("订单状态更新 - 订单ID: {}, 新状态: {}", orderId, newStatus);

        return toResponse(saved);
    }

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @throws BusinessException 当订单不存在或无法取消时抛出
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        updateOrderStatus(orderId, Order.Status.CANCELLED);
    }

    /**
     * 获取用户订单数量
     *
     * @param userId 用户ID
     * @return 订单数量
     */
    public Long getUserOrderCount(Long userId) {
        return orderDataAccessor.countByUserId(userId);
    }

    /**
     * 将订单实体转换为响应DTO
     *
     * @param order 订单实体
     * @return 订单响应DTO
     */
    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getUser().getId(),
            order.getUser().getUsername(),
            order.getProduct().getId(),
            order.getProduct().getName(),
            order.getQuantity(),
            order.getTotalPrice(),
            order.getStatus().name(),
            order.getStatus().getDescription(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
}