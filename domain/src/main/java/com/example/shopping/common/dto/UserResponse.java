package com.example.shopping.common.dto;

import com.example.shopping.common.entity.User;

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
 * @param role      角色
 * @param createdAt 创建时间
 */
public record UserResponse(
    Long id,
    String username,
    String email,
    User.Role role,
    LocalDateTime createdAt
) {}
