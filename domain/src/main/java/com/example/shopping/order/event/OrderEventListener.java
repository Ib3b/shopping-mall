package com.example.shopping.order.event;

import com.example.shopping.order.port.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 订单事件监听器
 * <p>
 * 在事务提交后异步发送通知。事件只携带不可变快照，监听器不访问
 * JPA 实体或懒加载代理，因此不会跨线程使用 Session。
 * </p>
 */
@Component
public class OrderEventListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventListener.class);

    private final NotificationSender notificationSender;

    public OrderEventListener(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    /**
     * 处理订单创建事件（提交后异步执行）
     *
     * @param event 订单创建事件
     */
    @Async("mailExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        OrderNotification notification = event.getNotification();
        logger.info("收到订单创建事件 - 订单ID: {}", notification.orderId());
        notificationSender.sendOrderConfirmation(notification);
    }

    /**
     * 处理订单状态变更事件（提交后异步执行）
     *
     * @param event 订单状态变更事件
     */
    @Async("mailExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        OrderNotification notification = event.getNotification();
        logger.info("收到订单状态变更事件 - 订单ID: {}, {} -> {}",
            notification.orderId(), event.getOldStatus(), event.getNewStatus());
        notificationSender.sendOrderStatusUpdate(notification);
    }
}
