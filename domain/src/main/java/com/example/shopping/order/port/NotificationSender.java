package com.example.shopping.order.port;

import com.example.shopping.common.entity.Order;

/**
 * 通知发送端口接口
 * <p>
 * 定义领域层所需的通知发送能力，由外层模块（如 web）实现。
 * 遵循依赖倒置原则，领域层不直接依赖基础设施。
 * </p>
 */
public interface NotificationSender {

    /**
     * 发送订单确认通知
     *
     * @param order 订单实体
     */
    void sendOrderConfirmation(Order order);

    /**
     * 发送订单状态更新通知
     *
     * @param order 订单实体
     */
    void sendOrderStatusUpdate(Order order);
}
