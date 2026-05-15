package com.example.shopping.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "商品更新请求（所有字段可选）")
public record ProductUpdateRequest(
    @Schema(description = "商品名称", example = "iPhone 15 Pro Max")
    @Size(max = 200, message = "商品名称不能超过200字符")
    String name,

    @Schema(description = "商品描述")
    String description,

    @Schema(description = "商品价格", example = "9999.00")
    @Positive(message = "价格必须为正数")
    BigDecimal price,

    @Schema(description = "库存数量", example = "80")
    @PositiveOrZero(message = "库存不能为负数")
    Integer stock,

    @Schema(description = "商品分类", example = "Electronics")
    @Size(max = 50, message = "分类名称不能超过50字符")
    String category
) {}
