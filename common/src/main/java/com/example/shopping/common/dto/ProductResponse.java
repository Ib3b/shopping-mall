package com.example.shopping.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品响应 DTO
 * <p>
 * 用于返回商品详细信息。
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
@Schema(description = "商品响应")
public record ProductResponse(
    @Schema(description = "商品ID", example = "1")
    Long id,

    @Schema(description = "商品名称", example = "iPhone 15")
    String name,

    @Schema(description = "商品描述", example = "苹果最新款手机")
    String description,

    @Schema(description = "商品价格", example = "6999.00")
    BigDecimal price,

    @Schema(description = "库存数量", example = "100")
    Integer stock,

    @Schema(description = "商品分类", example = "电子产品")
    String category,

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    LocalDateTime createdAt,

    @Schema(description = "更新时间", example = "2024-01-02T10:00:00")
    LocalDateTime updatedAt
) {}