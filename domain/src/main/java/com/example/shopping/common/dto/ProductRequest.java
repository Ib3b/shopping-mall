package com.example.shopping.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * 商品创建/更新请求 DTO
 * <p>
 * 用于接收商品创建和更新的请求数据。
 * </p>
 *
 * @param name        商品名称
 * @param description 商品描述
 * @param price       商品价格（必须为正数）
 * @param stock       库存数量（不能为负数）
 * @param category    商品分类
 */
public record ProductRequest(
    @NotBlank(message = "商品名称不能为空")
    String name,

    String description,

    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须为正数")
    BigDecimal price,

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    Integer stock,

    @NotBlank(message = "分类不能为空")
    String category
) {}