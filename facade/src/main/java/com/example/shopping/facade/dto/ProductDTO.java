package com.example.shopping.facade.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品响应DTO
 */
public record ProductDTO(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    String category,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}