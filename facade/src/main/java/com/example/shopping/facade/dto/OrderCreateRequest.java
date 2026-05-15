package com.example.shopping.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "订单创建请求")
public record OrderCreateRequest(
    @Schema(description = "用户ID", example = "1")
    @NotNull(message = "用户ID不能为空")
    Long userId,

    @Schema(description = "商品ID", example = "1")
    @NotNull(message = "商品ID不能为空")
    Long productId,

    @Schema(description = "购买数量", example = "2")
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    Integer quantity
) {}
