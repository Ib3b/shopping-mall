package com.example.shopping.order.port;

import com.example.shopping.order.event.OrderNotification;

/**
 * 通知发送端口接口
 * <p>
 * 定义领域层所需的通知发送能力，由外层模块（如 web）实现。
 * 遵循依赖倒置原则，领域层不直接依赖基础设施；入参是不可变快照，
 * 不携带 JPA 实体，可安全用于异步发送。
 * </p>
 */
public interface NotificationSender {

    /**
     * 发送订单确认通知
     *
     * @param notification 订单通知快照
     */
    void sendOrderConfirmation(OrderNotification notification);

    /**
     * 发送订单状态更新通知
     *
     * @param notification 订单通知快照
     */
    void sendOrderStatusUpdate(OrderNotification notification);
}
