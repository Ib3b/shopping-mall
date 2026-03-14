package com.example.shopping.facade.dto;

/**
 * 订单创建请求
 */
public record OrderCreateRequest(
    Long userId,
    Long productId,
    Integer quantity
) {}