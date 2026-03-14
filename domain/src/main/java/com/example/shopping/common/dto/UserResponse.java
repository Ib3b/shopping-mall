package com.example.shopping.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 用户响应 DTO
 * <p>
 * 用于返回用户信息，不包含敏感信息（如密码）。
 * </p>
 *
 * @param id        用户ID
 * @param username  用户名
 * @param email     邮箱地址
 * @param createdAt 创建时间
 */
@Schema(description = "用户响应")
public record UserResponse(
    @Schema(description = "用户ID", example = "1")
    Long id,

    @Schema(description = "用户名", example = "john")
    String username,

    @Schema(description = "邮箱地址", example = "john@example.com")
    String email,

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    LocalDateTime createdAt
) {}