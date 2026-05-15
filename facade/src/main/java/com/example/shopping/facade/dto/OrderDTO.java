package com.example.shopping.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "订单响应")
public record OrderDTO(
    @Schema(description = "订单ID", example = "1") Long id,
    @Schema(description = "用户ID", example = "1") Long userId,
    @Schema(description = "用户名", example = "user1") String username,
    @Schema(description = "商品ID", example = "1") Long productId,
    @Schema(description = "商品名称", example = "iPhone 15 Pro") String productName,
    @Schema(description = "购买数量", example = "2") Integer quantity,
    @Schema(description = "订单总价", example = "17998.00") BigDecimal totalPrice,
    @Schema(description = "订单状态", example = "PENDING") String status,
    @Schema(description = "订单状态描述", example = "待支付") String statusDesc,
    @Schema(description = "创建时间") LocalDateTime createdAt,
    @Schema(description = "更新时间") LocalDateTime updatedAt
) {}
