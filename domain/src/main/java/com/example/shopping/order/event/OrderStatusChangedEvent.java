package com.example.shopping.order.event;

import com.example.shopping.common.entity.Order;

/**
 * 订单状态变更事件
 * <p>
 * 订单状态成功变更后发布，携带不可变快照及变更前后的状态。
 * </p>
 */
public class OrderStatusChangedEvent extends OrderEvent {

    private final Order.Status oldStatus;
    private final Order.Status newStatus;

    public OrderStatusChangedEvent(OrderNotification notification,
                                   Order.Status oldStatus,
                                   Order.Status newStatus) {
        super(notification);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public Order.Status getOldStatus() {
        return oldStatus;
    }

    public Order.Status getNewStatus() {
        return newStatus;
    }

    @Override
    public String getEventType() {
        return "ORDER_STATUS_CHANGED";
    }
}
