package com.example.shopping.facade.dto;

import java.time.LocalDateTime;

/**
 * 用户响应 DTO
 * <p>
 * 用于用户查询接口的响应数据传输。
 * </p>
 *
 * @param id        用户ID
 * @param username  用户名
 * @param email     邮箱地址
 * @param createdAt 创建时间
 */
public record UserDTO(
    Long id,
    String username,
    String email,
    LocalDateTime createdAt
) {}