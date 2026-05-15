package com.example.shopping.facade.dto;

public record LoginResponse(
    String token,
    UserDTO user
) {}
