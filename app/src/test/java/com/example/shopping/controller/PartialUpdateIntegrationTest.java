package com.example.shopping.controller;

import com.example.shopping.common.entity.Product;
import com.example.shopping.common.entity.User;
import com.example.shopping.common.repository.OrderRepository;
import com.example.shopping.common.repository.ProductRepository;
import com.example.shopping.common.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 部分更新语义与错误映射的端到端回归测试。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PartialUpdateIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CacheManager cacheManager;

    private User admin;
    private Product product;
    private String token;

    @BeforeEach
    void setUp() throws Exception {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });

        admin = new User("partial_admin", "partial@test.com",
            passwordEncoder.encode("password123"));
        admin.setRole(User.Role.ADMIN);
        admin = userRepository.save(admin);

        product = productRepository.save(new Product(
            "partial-product", "original description",
            new BigDecimal("10.00"), 20, "test"));

        token = login("partial_admin", "password123");
    }

    @Test
    void passwordOnlyUpdateKeepsUsernameAndEmail() throws Exception {
        mockMvc.perform(put("/api/users/" + admin.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"password":"newPassword123"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("partial_admin"))
            .andExpect(jsonPath("$.email").value("partial@test.com"));

        User updated = userRepository.findById(admin.getId()).orElseThrow();
        assertEquals("partial_admin", updated.getUsername());
        assertEquals("partial@test.com", updated.getEmail());
        assertTrue(passwordEncoder.matches("newPassword123", updated.getPassword()));
    }

    @Test
    void priceOnlyUpdateKeepsOtherProductFields() throws Exception {
        mockMvc.perform(put("/api/products/" + product.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"price":123.45}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("partial-product"))
            .andExpect(jsonPath("$.stock").value(20))
            .andExpect(jsonPath("$.category").value("test"))
            .andExpect(jsonPath("$.price").value(123.45));

        Product updated = productRepository.findById(product.getId()).orElseThrow();
        assertEquals("partial-product", updated.getName());
        assertEquals(20, updated.getStock());
        assertEquals(new BigDecimal("123.45"), updated.getPrice());
    }

    @Test
    void oversizedPageSizeReturns400() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .get("/api/products?size=101")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_PARAMETER"));
    }

    @Test
    void unknownPathReturns404() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .get("/api/does-not-exist")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void malformedJsonReturns400() throws Exception {
        mockMvc.perform(put("/api/products/" + product.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"price\": "))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("MALFORMED_JSON"));
    }

    private String login(String username, String password) throws Exception {
        String body = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("token").asText();
    }
}
