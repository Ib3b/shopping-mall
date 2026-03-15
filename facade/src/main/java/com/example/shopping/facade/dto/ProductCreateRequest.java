package com.example.shopping.facade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 商品创建请求
 *
 * @param name        商品名称
 * @param description 商品描述
 * @param price       商品价格
 * @param stock       库存数量
 * @param category    商品分类
 */
public record ProductCreateRequest(
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称不能超过200字符")
    String name,

    String description,

    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须为正数")
    BigDecimal price,

    @NotNull(message = "库存不能为空")
    @PositiveOrZero(message = "库存不能为负数")
    Integer stock,

    @NotBlank(message = "分类不能为空")
    @Size(max = 50, message = "分类名称不能超过50字符")
    String category
) {}