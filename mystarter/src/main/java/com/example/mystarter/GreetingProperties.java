package com.example.mystarter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置属性类
 * 用户可以在 application.yml 中配置 myapp.greeting.*
 */
@ConfigurationProperties(prefix = "myapp.greeting")
public class GreetingProperties {

    /**
     * 问候语前缀
     */
    private String prefix = "Hello";

    /**
     * 是否启用
     */
    private boolean enabled = true;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}