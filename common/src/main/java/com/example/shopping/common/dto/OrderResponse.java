package com.example.shopping.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单响应 DTO
 * <p>
 * 用于返回订单详细信息。
 * </p>
 *
 * @param id                订单ID
 * @param userId            用户ID
 * @param username          用户名
 * @param productId         商品ID
 * @param productName       商品名称
 * @param quantity          购买数量
 * @param totalPrice        订单总金额
 * @param status            订单状态
 * @param statusDescription 订单状态描述
 * @param createdAt         创建时间
 * @param updatedAt         更新时间
 */
@Schema(description = "订单响应")
public record OrderResponse(
    @Schema(description = "订单ID", example = "1")
    Long id,

    @Schema(description = "用户ID", example = "1")
    Long userId,

    @Schema(description = "用户名", example = "john")
    String username,

    @Schema(description = "商品ID", example = "1")
    Long productId,

    @Schema(description = "商品名称", example = "iPhone 15")
    String productName,

    @Schema(description = "购买数量", example = "2")
    Integer quantity,

    @Schema(description = "订单总金额", example = "13998.00")
    BigDecimal totalPrice,

    @Schema(description = "订单状态", example = "PENDING")
    String status,

    @Schema(description = "订单状态描述", example = "待处理")
    String statusDescription,

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    LocalDateTime createdAt,

    @Schema(description = "更新时间", example = "2024-01-02T10:00:00")
    LocalDateTime updatedAt
) {}