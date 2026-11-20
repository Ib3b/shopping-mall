package com.example.shopping.web.config;

import com.example.shopping.common.security.TokenClaims;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTokenProviderTest {

    private static final String SECRET = "unit-test-secret-key-at-least-32-bytes";

    @Test
    void shouldRejectTooShortSecret() {
        assertThrows(IllegalStateException.class,
            () -> new JwtTokenProvider("too-short", 60_000));
    }

    @Test
    void shouldIssueAndParseRoleClaim() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 60_000);

        String token = provider.createToken(42L, "alice", "ADMIN");
        TokenClaims claims = provider.parseToken(token);

        assertNotNull(claims);
        assertEquals(42L, claims.userId());
        assertEquals("ADMIN", claims.role());
    }

    @Test
    void shouldReturnNullForTamperedToken() {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, 60_000);

        String token = provider.createToken(1L, "bob", "USER");

        assertNull(provider.parseToken(token + "x"));
    }
}
