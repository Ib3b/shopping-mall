package com.example.shopping.order.state;

import com.example.shopping.common.entity.Order;
import org.springframework.stereotype.Component;

/**
 * 已支付状态处理器
 * <p>
 * 已支付状态下：
 * <ul>
 *   <li>可转换为：已发货（商家发货）</li>
 *   <li>可转换为：已取消（商家取消，退款）</li>
 * </ul>
 * </p>
 */
@Component
public class PaidStateHandler implements OrderStateHandler {

    @Override
    public Order.Status getStatus() {
        return Order.Status.PAID;
    }

    @Override
    public boolean canTransitionTo(Order.Status targetStatus) {
        return targetStatus == Order.Status.SHIPPED ||
               targetStatus == Order.Status.CANCELLED;
    }
}