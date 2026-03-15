package com.example.shopping.order.state;

import com.example.shopping.common.entity.Order;
import org.springframework.stereotype.Component;

/**
 * 已取消状态处理器
 * <p>
 * 已取消为终态，不可再转换到其他状态。
 * </p>
 */
@Component
public class CancelledStateHandler implements OrderStateHandler {

    @Override
    public Order.Status getStatus() {
        return Order.Status.CANCELLED;
    }

    @Override
    public boolean canTransitionTo(Order.Status targetStatus) {
        // 已取消是终态，不允许转换
        return false;
    }
}