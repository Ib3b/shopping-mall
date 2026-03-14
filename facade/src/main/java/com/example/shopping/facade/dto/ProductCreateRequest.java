package com.example.shopping.facade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 商品创建请求
 */
public record ProductCreateRequest(
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称最长100字符")
    String name,

    @Size(max = 500, message = "商品描述最长500字符")
    String description,

    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须大于0")
    BigDecimal price,

    @NotNull(message = "库存不能为空")
    @Positive(message = "库存必须大于0")
    Integer stock,

    @Size(max = 50, message = "分类最长50字符")
    String category
) {}