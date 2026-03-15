package com.example.shopping.order.event;

import com.example.shopping.common.entity.Order;

/**
 * 订单事件基类
 * <p>
 * 所有订单相关事件的基类，包含订单实体引用。
 * 使用观察者模式实现订单状态变更的通知机制。
 * </p>
 */
public abstract class OrderEvent {

    private final Order order;

    protected OrderEvent(Order order) {
        this.order = order;
    }

    /**
     * 获取订单实体
     *
     * @return 订单实体
     */
    public Order getOrder() {
        return order;
    }

    /**
     * 获取事件类型描述
     *
     * @return 事件类型描述
     */
    public abstract String getEventType();
}