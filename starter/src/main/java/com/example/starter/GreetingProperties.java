package com.example.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 问候服务配置属性
 * <p>
 * 用户可以在 {@code application.yml} 中配置 {@code myapp.greeting.*} 属性。
 * </p>
 */
@ConfigurationProperties(prefix = "myapp.greeting")
public class GreetingProperties {

    /**
     * 问候语前缀
     */
    private String prefix = "Hello";

    /**
     * 是否启用问候服务
     */
    private boolean enabled = true;

    /**
     * 获取问候语前缀
     *
     * @return 问候语前缀
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * 设置问候语前缀
     *
     * @param prefix 问候语前缀
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * 判断服务是否启用
     *
     * @return 是否启用
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置服务启用状态
     *
     * @param enabled 是否启用
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}