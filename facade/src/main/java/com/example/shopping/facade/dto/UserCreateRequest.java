package com.example.shopping.facade.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户创建请求
 *
 * @param username 用户名
 * @param email    邮箱地址
 * @param password 密码
 */
public record UserCreateRequest(
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    String username,

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    String email,

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100之间")
    String password
) {}