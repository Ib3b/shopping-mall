package com.example.shopping.common.dto;

import com.example.shopping.common.entity.User;

/**
 * 认证成功的用户信息
 *
 * @param id       用户ID
 * @param username 用户名
 * @param role     角色
 */
public record AuthenticatedUser(Long id, String username, User.Role role) {
}
