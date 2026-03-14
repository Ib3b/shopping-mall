package com.example.shopping.facade.dto;

import java.math.BigDecimal;

/**
 * 商品创建请求
 */
public record ProductCreateRequest(
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    String category
) {}