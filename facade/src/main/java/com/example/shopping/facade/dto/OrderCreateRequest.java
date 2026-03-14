package com.example.shopping.facade.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 订单创建请求
 */
public record OrderCreateRequest(
    @NotNull(message = "用户ID不能为空")
    @Positive(message = "用户ID必须大于0")
    Long userId,

    @NotNull(message = "商品ID不能为空")
    @Positive(message = "商品ID必须大于0")
    Long productId,

    @NotNull(message = "数量不能为空")
    @Positive(message = "数量必须大于0")
    Integer quantity
) {}