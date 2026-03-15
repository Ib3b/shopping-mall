package com.example.shopping.order.state;

import com.example.shopping.common.entity.Order;

/**
 * 订单状态处理器接口
 * <p>
 * 使用状态模式定义订单状态的转换规则。
 * 每个状态实现类决定该状态下允许的转换目标状态。
 * </p>
 */
public interface OrderStateHandler {

    /**
     * 获取此处理器对应的状态
     *
     * @return 订单状态
     */
    Order.Status getStatus();

    /**
     * 判断是否可以转换到指定状态
     *
     * @param targetStatus 目标状态
     * @return 是否允许转换
     */
    boolean canTransitionTo(Order.Status targetStatus);

    /**
     * 执行状态转换前的操作
     * <p>
     * 例如：取消订单时恢复库存
     * </p>
     *
     * @param order       订单实体
     * @param targetStatus 目标状态
     */
    default void beforeTransition(Order order, Order.Status targetStatus) {
        // 默认不做任何操作
    }

    /**
     * 获取状态描述
     *
     * @return 状态描述
     */
    default String getDescription() {
        return getStatus().getDescription();
    }
}