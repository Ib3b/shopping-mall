package com.example.shopping.order.state;

import com.example.shopping.common.entity.Order;
import org.springframework.stereotype.Component;

/**
 * 已发货状态处理器
 * <p>
 * 已发货状态下：
 * <ul>
 *   <li>可转换为：已送达（物流送达）</li>
 * </ul>
 * </p>
 */
@Component
public class ShippedStateHandler implements OrderStateHandler {

    @Override
    public Order.Status getStatus() {
        return Order.Status.SHIPPED;
    }

    @Override
    public boolean canTransitionTo(Order.Status targetStatus) {
        return targetStatus == Order.Status.DELIVERED;
    }
}