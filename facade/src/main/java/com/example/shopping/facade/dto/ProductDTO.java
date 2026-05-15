package com.example.shopping.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "商品响应")
public record ProductDTO(
    @Schema(description = "商品ID", example = "1") Long id,
    @Schema(description = "商品名称", example = "iPhone 15 Pro") String name,
    @Schema(description = "商品描述") String description,
    @Schema(description = "商品价格", example = "8999.00") BigDecimal price,
    @Schema(description = "库存数量", example = "50") Integer stock,
    @Schema(description = "商品分类", example = "Electronics") String category,
    @Schema(description = "创建时间") LocalDateTime createdAt,
    @Schema(description = "更新时间") LocalDateTime updatedAt
) {}
