package com.example.starter;

/**
 * 问候服务
 * <p>
 * 提供简单的问候语生成功能，用于演示自定义 Spring Boot Starter 的开发。
 * 可通过配置 {@code myapp.greeting.prefix} 自定义问候语前缀。
 * </p>
 */
public class GreetingService {

    private final GreetingProperties properties;

    public GreetingService(GreetingProperties properties) {
        this.properties = properties;
    }

    /**
     * 生成问候语
     *
     * @param name 被问候者的名称
     * @return 格式化的问候语，如 "Hello, World!"
     *         如果服务已禁用则返回空字符串
     */
    public String greet(String name) {
        if (!properties.isEnabled()) {
            return "";
        }
        return properties.getPrefix() + ", " + name + "!";
    }
}