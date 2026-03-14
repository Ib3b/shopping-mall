package com.example.shopping.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户创建/更新请求 DTO
 * <p>
 * 用于接收用户注册和更新的请求数据。
 * </p>
 *
 * @param username 用户名（3-50个字符）
 * @param email    邮箱地址
 * @param password 密码（6-100个字符）
 */
@Schema(description = "用户创建/更新请求")
public record UserRequest(
    @Schema(description = "用户名", example = "john", minLength = 3, maxLength = 50)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    String username,

    @Schema(description = "邮箱地址", example = "john@example.com")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    String email,

    @Schema(description = "密码", example = "123456", minLength = 6, maxLength = 100)
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100之间")
    String password
) {}