package com.example.shopping.web.config;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 当前登录用户访问器。
 * <p>
 * JWT 过滤器把用户ID作为 principal、角色写入 authorities。
 * </p>
 */
@Component
public class CurrentUser {

    /**
     * 获取当前用户ID。
     *
     * @return 用户ID
     * @throws AccessDeniedException 未认证时抛出
     */
    public Long id() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Long userId)) {
            throw new AccessDeniedException("未认证");
        }
        return userId;
    }

    /**
     * 当前用户是否为管理员。
     *
     * @return 是否管理员
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
            .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    /**
     * 要求当前用户是资源所有者或管理员。
     *
     * @param ownerId 资源所有者ID
     * @throws AccessDeniedException 不满足条件时抛出
     */
    public void requireSelfOrAdmin(Long ownerId) {
        if (!isAdmin() && !id().equals(ownerId)) {
            throw new AccessDeniedException("无权访问该资源");
        }
    }
}
