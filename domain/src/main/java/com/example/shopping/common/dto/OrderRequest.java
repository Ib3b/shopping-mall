package com.example.shopping.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 订单创建请求 DTO
 * <p>
 * 用于接收订单创建的请求数据。
 * </p>
 *
 * @param userId    用户ID
 * @param productId 商品ID
 * @param quantity  购买数量（必须大于0）
 */
public record OrderRequest(
    @NotNull(message = "用户ID不能为空")
    Long userId,

    @NotNull(message = "商品ID不能为空")
    Long productId,

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    Integer quantity
) {}