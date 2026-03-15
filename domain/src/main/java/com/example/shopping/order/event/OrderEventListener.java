package com.example.shopping.order.event;

import com.example.shopping.common.entity.Order;
import com.example.shopping.order.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 订单事件监听器
 * <p>
 * 监听订单相关事件，触发相应的邮件通知。
 * 使用观察者模式实现业务逻辑解耦。
 * </p>
 */
@Component
public class OrderEventListener {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventListener.class);

    private final MailService mailService;

    public OrderEventListener(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * 处理订单创建事件
     * <p>
     * 异步发送订单确认邮件
     * </p>
     *
     * @param event 订单创建事件
     */
    @Async("mailExecutor")
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        Order order = event.getOrder();
        logger.info("收到订单创建事件 - 订单ID: {}", order.getId());
        mailService.sendOrderConfirmation(order);
    }

    /**
     * 处理订单状态变更事件
     * <p>
     * 异步发送状态更新通知邮件
     * </p>
     *
     * @param event 订单状态变更事件
     */
    @Async("mailExecutor")
    @EventListener
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        Order order = event.getOrder();
        logger.info("收到订单状态变更事件 - 订单ID: {}, {} -> {}",
            order.getId(), event.getOldStatus(), event.getNewStatus());
        mailService.sendOrderStatusUpdate(order);
    }
}