package com.example.shopping.facade.dto;

import java.math.BigDecimal;

/**
 * 商品更新请求
 */
public record ProductUpdateRequest(
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    String category
) {}