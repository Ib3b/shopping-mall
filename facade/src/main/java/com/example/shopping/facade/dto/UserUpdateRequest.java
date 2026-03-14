package com.example.shopping.facade.dto;

/**
 * 用户更新请求
 */
public record UserUpdateRequest(
    String username,
    String email,
    String password
) {}