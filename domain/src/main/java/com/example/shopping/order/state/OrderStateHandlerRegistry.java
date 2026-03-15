package com.example.shopping.order.state;

import com.example.shopping.common.entity.Order;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 订单状态处理器注册表
 * <p>
 * 管理所有订单状态处理器，提供根据状态查找处理器的能力。
 * 使用状态模式实现状态转换逻辑的解耦。
 * </p>
 */
@Component
public class OrderStateHandlerRegistry {

    private final Map<Order.Status, OrderStateHandler> handlers = new EnumMap<>(Order.Status.class);

    /**
     * 通过依赖注入自动收集所有状态处理器
     *
     * @param handlerList 所有状态处理器列表
     */
    public OrderStateHandlerRegistry(List<OrderStateHandler> handlerList) {
        for (OrderStateHandler handler : handlerList) {
            handlers.put(handler.getStatus(), handler);
        }
    }

    /**
     * 根据状态获取对应的处理器
     *
     * @param status 订单状态
     * @return 状态处理器
     * @throws IllegalStateException 当找不到对应处理器时抛出
     */
    public OrderStateHandler getHandler(Order.Status status) {
        OrderStateHandler handler = handlers.get(status);
        if (handler == null) {
            throw new IllegalStateException("未找到状态处理器: " + status);
        }
        return handler;
    }

    /**
     * 判断状态转换是否合法
     *
     * @param currentStatus 当前状态
     * @param targetStatus  目标状态
     * @return 是否允许转换
     */
    public boolean canTransition(Order.Status currentStatus, Order.Status targetStatus) {
        return getHandler(currentStatus).canTransitionTo(targetStatus);
    }
}