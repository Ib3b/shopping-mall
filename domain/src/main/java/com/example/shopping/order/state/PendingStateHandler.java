package com.example.shopping.order.state;

import com.example.shopping.common.entity.Order;
import org.springframework.stereotype.Component;

/**
 * 待支付状态处理器
 * <p>
 * 待支付状态下：
 * <ul>
 *   <li>可转换为：已支付（用户付款）</li>
 *   <li>可转换为：已取消（用户取消）</li>
 * </ul>
 * </p>
 */
@Component
public class PendingStateHandler implements OrderStateHandler {

    @Override
    public Order.Status getStatus() {
        return Order.Status.PENDING;
    }

    @Override
    public boolean canTransitionTo(Order.Status targetStatus) {
        return targetStatus == Order.Status.PAID ||
               targetStatus == Order.Status.CANCELLED;
    }
}