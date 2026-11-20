package com.example.shopping.order.event;

/**
 * 订单事件基类
 * <p>
 * 所有订单相关事件的基类。事件只携带不可变快照，不持有 JPA 实体，
 * 因此可以在事务提交后安全地交给异步监听器使用。
 * </p>
 */
public abstract class OrderEvent {

    private final OrderNotification notification;

    protected OrderEvent(OrderNotification notification) {
        this.notification = notification;
    }

    /**
     * 获取订单通知快照
     *
     * @return 通知快照
     */
    public OrderNotification getNotification() {
        return notification;
    }

    /**
     * 获取事件类型描述
     *
     * @return 事件类型描述
     */
    public abstract String getEventType();
}
