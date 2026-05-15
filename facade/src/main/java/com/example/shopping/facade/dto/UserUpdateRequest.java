package com.example.shopping.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "用户更新请求（所有字段可选）")
public record UserUpdateRequest(
    @Schema(description = "用户名", example = "john_doe_new")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    String username,

    @Schema(description = "邮箱地址", example = "john_new@example.com")
    @Email(message = "邮箱格式不正确")
    String email,

    @Schema(description = "新密码（可选）", example = "newPassword123")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100之间")
    String password
) {}
