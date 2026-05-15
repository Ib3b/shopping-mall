package com.example.shopping.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "商品创建请求")
public record ProductCreateRequest(
    @Schema(description = "商品名称", example = "iPhone 15 Pro")
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称不能超过200字符")
    String name,

    @Schema(description = "商品描述", example = "Apple iPhone 15 Pro 256GB")
    String description,

    @Schema(description = "商品价格", example = "8999.00")
    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须为正数")
    BigDecimal price,

    @Schema(description = "库存数量", example = "100")
    @NotNull(message = "库存不能为空")
    @PositiveOrZero(message = "库存不能为负数")
    Integer stock,

    @Schema(description = "商品分类", example = "Electronics")
    @NotBlank(message = "分类不能为空")
    @Size(max = 50, message = "分类名称不能超过50字符")
    String category
) {}
