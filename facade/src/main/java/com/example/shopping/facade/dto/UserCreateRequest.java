package com.example.shopping.facade.dto;

/**
 * 用户创建请求
 */
public record UserCreateRequest(
    String username,
    String email,
    String password
) {}