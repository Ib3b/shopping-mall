package com.example.shopping.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

/**
 * 测试用认证对象：principal 为 Long，与 JWT 过滤器保持一致。
 */
final class TestAuth {

    private TestAuth() {
    }

    static RequestPostProcessor admin(Long userId) {
        return postProcessor(userId, "ADMIN");
    }

    static RequestPostProcessor user(Long userId) {
        return postProcessor(userId, "USER");
    }

    private static RequestPostProcessor postProcessor(Long userId, String role) {
        return SecurityMockMvcRequestPostProcessors.authentication(
            new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))));
    }
}
