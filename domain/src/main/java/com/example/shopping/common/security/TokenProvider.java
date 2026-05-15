package com.example.shopping.common.security;

/**
 * Token provider port interface.
 * Domain layer defines the contract, web layer provides the JWT implementation.
 */
public interface TokenProvider {

    /**
     * Create a JWT token for the given user.
     *
     * @param userId   user ID
     * @param username username
     * @return signed JWT token string
     */
    String createToken(Long userId, String username);

    /**
     * Extract user ID from a valid token.
     *
     * @param token JWT token string
     * @return user ID, or null if token is invalid/expired
     */
    Long getUserIdFromToken(String token);

    /**
     * Validate a token string.
     *
     * @param token JWT token string
     * @return true if valid, false otherwise
     */
    boolean validateToken(String token);
}
