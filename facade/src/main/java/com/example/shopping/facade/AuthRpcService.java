package com.example.shopping.facade;

import com.example.shopping.facade.dto.UserCreateRequest;
import com.example.shopping.facade.dto.UserDTO;

public interface AuthRpcService {

    /**
     * Authenticate user and return JWT token.
     */
    String login(String username, String password);

    /**
     * Register a new user and return JWT token.
     */
    String register(UserCreateRequest request);

    /**
     * Get current authenticated user by user ID stored in token.
     */
    UserDTO getCurrentUser(Long userId);
}
