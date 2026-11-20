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
     * @param role     role name (USER / ADMIN)
     * @return signed JWT token string
     */
    String createToken(Long userId, String username, String role);

    /**
     * Parse and validate a token in one pass.
     *
     * @param token JWT token string
     * @return claims, or {@code null} if the token is invalid/expired
     */
    TokenClaims parseToken(String token);
}
