package com.example.shopping.order.event;

import com.example.shopping.common.entity.Order;

import java.math.BigDecimal;

/**
 * 订单通知快照
 * <p>
 * 在事务内构建、只包含已初始化的值，供异步通知使用。
 * 避免把 JPA 实体或懒加载代理带出事务边界。
 * </p>
 */
public record OrderNotification(
    Long orderId,
    Long userId,
    String username,
    String email,
    Long productId,
    String productName,
    int quantity,
    BigDecimal totalPrice,
    String status,
    String statusDescription
) {

    /**
     * 从已加载的订单实体构建快照（必须在事务内调用）。
     *
     * @param order 订单实体
     * @return 通知快照
     */
    public static OrderNotification from(Order order) {
        return new OrderNotification(
            order.getId(),
            order.getUser().getId(),
            order.getUser().getUsername(),
            order.getUser().getEmail(),
            order.getProduct().getId(),
            order.getProduct().getName(),
            order.getQuantity(),
            order.getTotalPrice(),
            order.getStatus().name(),
            order.getStatus().getDescription()
        );
    }
}
