package com.example.shopping.facade.dto;

import java.time.LocalDateTime;

/**
 * 用户响应DTO
 */
public record UserDTO(
    Long id,
    String username,
    String email,
    LocalDateTime createdAt
) {}