package com.example.shopping.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Swagger/OpenAPI 配置类
 * <p>
 * 配置 API 文档信息，包括标题、描述、版本等。
 * </p>
 */
@Configuration
public class SwaggerConfig {

    /**
     * 创建 OpenAPI 配置
     *
     * @return OpenAPI 实例
     */
    @Bean
    public OpenAPI shoppingMallOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("网购商城API")
                .description("完整的网购商城后端REST API接口文档")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Shopping Mall Team")
                    .email("support@shopping-mall.com")))
            .tags(Arrays.asList(
                new Tag().name("用户管理").description("用户注册、登录、查询接口"),
                new Tag().name("商品管理").description("商品CRUD、库存管理接口"),
                new Tag().name("订单管理").description("订单创建、查询、状态管理接口")
            ));
    }
}