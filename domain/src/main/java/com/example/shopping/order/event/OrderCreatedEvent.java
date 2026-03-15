package com.example.shopping.order.event;

import com.example.shopping.common.entity.Order;

/**
 * 订单创建事件
 * <p>
 * 当订单创建成功后发布此事件，用于触发确认邮件发送等后续操作。
 * </p>
 */
public class OrderCreatedEvent extends OrderEvent {

    public OrderCreatedEvent(Order order) {
        super(order);
    }

    @Override
    public String getEventType() {
        return "ORDER_CREATED";
    }
}