package com.example.shopping.facade.enums;

/**
 * 订单状态枚举
 * <p>
 * 定义订单的生命周期状态，用于 RPC 接口层的状态传递。
 * </p>
 */
public enum OrderStatus {
    /** 待支付 - 订单已创建，等待用户支付 */
    PENDING("待支付"),
    /** 已支付 - 用户已完成支付，等待发货 */
    PAID("已支付"),
    /** 已发货 - 商品已发出，等待送达 */
    SHIPPED("已发货"),
    /** 已送达 - 商品已送达用户，订单完成 */
    DELIVERED("已送达"),
    /** 已取消 - 订单已取消，库存已恢复 */
    CANCELLED("已取消");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    /**
     * 获取状态描述
     *
     * @return 状态的中文描述
     */
    public String getDescription() {
        return description;
    }
}