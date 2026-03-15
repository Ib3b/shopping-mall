package com.example.shopping.facade.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品响应 DTO
 * <p>
 * 用于商品查询接口的响应数据传输。
 * </p>
 *
 * @param id          商品ID
 * @param name        商品名称
 * @param description 商品描述
 * @param price       商品价格
 * @param stock       库存数量
 * @param category    商品分类
 * @param createdAt   创建时间
 * @param updatedAt   更新时间
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