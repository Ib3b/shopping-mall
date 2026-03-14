package com.example.mystarter;

public class GreetingService {

    private final GreetingProperties properties;

    public GreetingService(GreetingProperties properties) {
        this.properties = properties;
    }

    /**
     * 生成问候语
     */
    public String greet(String name) {
        if (!properties.isEnabled()) {
            return "";
        }
        return properties.getPrefix() + ", " + name + "!";
    }
}