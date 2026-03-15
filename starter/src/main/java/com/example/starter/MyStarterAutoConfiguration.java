package com.example.starter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义 Starter 的自动配置类
 * <p>
 * 当其他项目依赖这个 starter 时，会自动创建 {@link GreetingService} Bean。
 * 配置属性通过 {@link GreetingProperties} 绑定。
 * </p>
 */
@Configuration
public class MyStarterAutoConfiguration {

    /**
     * 创建配置属性 Bean
     *
     * @return 配置属性实例
     */
    @Bean
    public GreetingProperties greetingProperties() {
        return new GreetingProperties();
    }

    /**
     * 创建问候服务 Bean
     *
     * @param properties 配置属性
     * @return 问候服务实例
     */
    @Bean
    public GreetingService greetingService(GreetingProperties properties) {
        return new GreetingService(properties);
    }
}