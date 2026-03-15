package com.example.shopping.facade.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 商品更新请求
 *
 * @param name        商品名称
 * @param description 商品描述
 * @param price       商品价格
 * @param stock       库存数量
 * @param category    商品分类
 */
public record ProductUpdateRequest(
    @Size(max = 200, message = "商品名称不能超过200字符")
    String name,

    String description,

    @Positive(message = "价格必须为正数")
    BigDecimal price,

    @PositiveOrZero(message = "库存不能为负数")
    Integer stock,

    @Size(max = 50, message = "分类名称不能超过50字符")
    String category
) {}