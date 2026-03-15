package com.example.shopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 网购商城应用程序入口类
 * <p>
 * 启动 Spring Boot 应用程序，启用异步处理功能。
 * </p>
 */
@SpringBootApplication
@EnableAsync
public class ShoppingApplication {

    /**
     * 应用程序主入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(ShoppingApplication.class, args);
    }
}