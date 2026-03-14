package com.example.starter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义 Starter 的自动配置类
 * 当其他项目依赖这个 starter 时，会自动创建这个 Bean
 */
@Configuration
public class MyStarterAutoConfiguration {

    /**
     * 创建配置属性类（@ConfigurationProperties 在 GreetingProperties 类上）
     */
    @Bean
    public GreetingProperties greetingProperties() {
        return new GreetingProperties();
    }

    /**
     *
     * 创建服务 Bean
     */
    @Bean
    public GreetingService greetingService(GreetingProperties properties) {
        return new GreetingService(properties);
    }
}