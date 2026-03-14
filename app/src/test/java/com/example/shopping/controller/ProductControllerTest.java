package com.example.shopping.controller;

import com.example.shopping.common.dto.ProductResponse;
import com.example.shopping.facade.ProductRpcService;
import com.example.shopping.facade.dto.ProductCreateRequest;
import com.example.shopping.facade.dto.ProductDTO;
import com.example.shopping.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 商品控制器测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductRpcService productRpcService;

    @MockitoBean
    private ProductService productService;

    @Test
    void shouldCreateProduct() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest("新商品", "描述", new BigDecimal("199.99"), 50, "分类");
        ProductDTO response = new ProductDTO(1L, "新商品", "描述", new BigDecimal("199.99"), 50, "分类",
            LocalDateTime.now(), LocalDateTime.now());

        when(productRpcService.createProduct(any(ProductCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("新商品"));
    }

    @Test
    void shouldGetProductById() throws Exception {
        ProductDTO response = new ProductDTO(1L, "测试商品", "描述", new BigDecimal("99.99"), 100, "电子产品",
            LocalDateTime.now(), LocalDateTime.now());

        when(productRpcService.getProductById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("测试商品"));
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        ProductResponse product1 = new ProductResponse(1L, "商品1", "描述1", new BigDecimal("10"), 10, "分类1",
            LocalDateTime.now(), LocalDateTime.now());
        ProductResponse product2 = new ProductResponse(2L, "商品2", "描述2", new BigDecimal("20"), 20, "分类2",
            LocalDateTime.now(), LocalDateTime.now());

        Page<ProductResponse> page = new PageImpl<>(List.of(product1, product2));
        when(productService.getAllProducts(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void shouldGetProductsByCategory() throws Exception {
        ProductDTO response = new ProductDTO(1L, "电子商品", "描述", new BigDecimal("100"), 50, "电子产品",
            LocalDateTime.now(), LocalDateTime.now());

        when(productRpcService.getProductsByCategory("电子产品")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/products/category/电子产品"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].category").value("电子产品"));
    }

    @Test
    void shouldSearchProducts() throws Exception {
        ProductDTO response = new ProductDTO(1L, "iPhone15", "苹果手机", new BigDecimal("8999"), 50, "电子产品",
            LocalDateTime.now(), LocalDateTime.now());

        when(productRpcService.searchProducts("iPhone")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/products/search").param("keyword", "iPhone"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("iPhone15"));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
            .andExpect(status().isNoContent());
    }
}