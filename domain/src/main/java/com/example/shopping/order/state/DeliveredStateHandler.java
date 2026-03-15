package com.example.shopping.order.state;

import com.example.shopping.common.entity.Order;
import org.springframework.stereotype.Component;

/**
 * 已送达状态处理器
 * <p>
 * 已送达为终态，不可再转换到其他状态。
 * </p>
 */
@Component
public class DeliveredStateHandler implements OrderStateHandler {

    @Override
    public Order.Status getStatus() {
        return Order.Status.DELIVERED;
    }

    @Override
    public boolean canTransitionTo(Order.Status targetStatus) {
        // 已送达是终态，不允许转换
        return false;
    }
}