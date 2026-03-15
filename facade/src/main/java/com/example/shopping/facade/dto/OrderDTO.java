package com.example.shopping.facade.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单响应 DTO
 * <p>
 * 用于订单查询接口的响应数据传输。
 * </p>
 *
 * @param id           订单ID
 * @param userId       用户ID
 * @param username     用户名
 * @param productId    商品ID
 * @param productName  商品名称
 * @param quantity     购买数量
 * @param totalPrice   订单总价
 * @param status       订单状态（英文）
 * @param statusDesc   订单状态描述（中文）
 * @param createdAt    创建时间
 * @param updatedAt    更新时间
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