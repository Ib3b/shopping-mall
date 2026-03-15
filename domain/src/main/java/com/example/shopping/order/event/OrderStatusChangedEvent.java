package com.example.shopping.order.event;

import com.example.shopping.common.entity.Order;

/**
 * 订单状态变更事件
 * <p>
 * 当订单状态变更后发布此事件，用于触发状态更新通知等后续操作。
 * </p>
 */
public class OrderStatusChangedEvent extends OrderEvent {

    private final Order.Status oldStatus;
    private final Order.Status newStatus;

    public OrderStatusChangedEvent(Order order, Order.Status oldStatus, Order.Status newStatus) {
        super(order);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    @Override
    public String getEventType() {
        return "ORDER_STATUS_CHANGED";
    }

    /**
     * 获取变更前的状态
     *
     * @return 旧状态
     */
    public Order.Status getOldStatus() {
        return oldStatus;
    }

    /**
     * 获取变更后的状态
     *
     * @return 新状态
     */
    public Order.Status getNewStatus() {
        return newStatus;
    }
}