package com.example.shopping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * 商品创建/更新请求 DTO
 * <p>
 * 用于接收商品创建和更新的请求数据。
 * </p>
 *
 * @param name        商品名称
 * @param description 商品描述
 * @param price       商品价格（必须为正数）
 * @param stock       库存数量（不能为负数）
 * @param category    商品分类
 */
@Schema(description = "商品创建/更新请求")
public record ProductRequest(
    @Schema(description = "商品名称", example = "iPhone 15")
    @NotBlank(message = "商品名称不能为空")
    String name,

    @Schema(description = "商品描述", example = "苹果最新款手机")
    String description,

    @Schema(description = "商品价格", example = "6999.00")
    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须为正数")
    BigDecimal price,

    @Schema(description = "库存数量", example = "100")
    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    Integer stock,

    @Schema(description = "商品分类", example = "电子产品")
    @NotBlank(message = "分类不能为空")
    String category
) {}