package com.example.shopping.facade.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * 用户更新请求
 */
public record UserUpdateRequest(
    @Size(min = 2, max = 50, message = "用户名长度2-50字符")
    String username,

    @Email(message = "邮箱格式不正确")
    String email,

    @Size(min = 6, max = 100, message = "密码长度6-100字符")
    String password
) {}