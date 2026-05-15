package com.example.shopping.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI shoppingMallOpenAPI() {
        String schemeName = "bearerAuth";
        return new OpenAPI()
            .info(new Info()
                .title("网购商城API")
                .description("完整的网购商城后端REST API接口文档")
                .version("1.1.0")
                .contact(new Contact()
                    .name("Shopping Mall Team")
                    .email("support@shopping-mall.com")))
            .tags(Arrays.asList(
                new Tag().name("用户管理").description("用户注册、登录、查询接口"),
                new Tag().name("商品管理").description("商品CRUD、库存管理接口"),
                new Tag().name("订单管理").description("订单创建、查询、状态管理接口"),
                new Tag().name("认证管理").description("用户登录、注册、当前用户查询")
            ))
            .components(new Components()
                .addSecuritySchemes(schemeName, new SecurityScheme()
                    .name(schemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("输入 JWT token，无需 Bearer 前缀")))
            .security(List.of(new SecurityRequirement().addList(schemeName)));
    }
}
