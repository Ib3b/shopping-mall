package com.example.shopping.controller;

import com.example.mystarter.GreetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 问候控制器
 * <p>
 * 使用自定义 Starter 提供问候服务。
 * </p>
 */
@RestController
@Tag(name = "问候服务", description = "自定义 Starter 示例接口")
public class GreetingController {

    private final GreetingService greetingService;

    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    /**
     * 生成问候语
     *
     * @param name 名字（默认为"游客"）
     * @return 问候语
     */
    @GetMapping("/greet")
    @Operation(summary = "问候", description = "生成个性化问候语")
    public String greet(@RequestParam(defaultValue = "游客") String name) {
        return greetingService.greet(name);
    }
}