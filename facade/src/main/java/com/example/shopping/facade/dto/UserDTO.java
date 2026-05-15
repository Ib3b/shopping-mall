package com.example.shopping.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "用户响应")
public record UserDTO(
    @Schema(description = "用户ID", example = "1") Long id,
    @Schema(description = "用户名", example = "john_doe") String username,
    @Schema(description = "邮箱地址", example = "john@example.com") String email,
    @Schema(description = "创建时间") LocalDateTime createdAt
) {}
