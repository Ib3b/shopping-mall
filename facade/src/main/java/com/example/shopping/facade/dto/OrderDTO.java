package com.example.shopping.facade.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单响应DTO
 */
public record OrderDTO(
    Long id,
    Long userId,
    String username,
    Long productId,
    String productName,
    Integer quantity,
    BigDecimal totalPrice,
    String status,
    String statusDesc,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}