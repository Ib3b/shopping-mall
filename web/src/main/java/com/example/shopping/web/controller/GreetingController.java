package com.example.shopping.web.controller;

import com.example.mystarter.GreetingService;
import org.springframework.web.bind.annotation.*;

/**
 * 问候控制器
 * <p>
 * 演示自定义 Starter 的使用
 * </p>
 */
@RestController
@RequestMapping("/api/greeting")
public class GreetingController {

    private final GreetingService greetingService;

    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping
    public String greet(@RequestParam(defaultValue = "World") String name) {
        return greetingService.greet(name);
    }
}