package com.example.shopping.common.security;

/**
 * 解析后的 JWT 声明。
 *
 * @param userId 用户ID
 * @param role   角色（USER / ADMIN）
 */
public record TokenClaims(Long userId, String role) {
}
