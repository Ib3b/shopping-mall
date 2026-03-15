package com.example.shopping.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密配置
 * <p>
 * 提供 BCrypt 密码加密器，用于用户密码的安全存储。
 * </p>
 */
@Configuration
public class PasswordConfig {

    /**
     * 创建 BCrypt 密码加密器
     *
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}