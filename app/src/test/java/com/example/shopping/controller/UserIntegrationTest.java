package com.example.shopping.controller;

import com.example.shopping.common.entity.User;
import com.example.shopping.common.repository.OrderRepository;
import com.example.shopping.common.repository.ProductRepository;
import com.example.shopping.common.repository.UserRepository;
import com.example.shopping.facade.dto.LoginRequest;
import com.example.shopping.facade.dto.UserCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void cleanState() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
    }

    @Test
    void shouldRegisterAndLoginUser() throws Exception {
        // Register
        UserCreateRequest regReq = new UserCreateRequest("inttest_user", "inttest@test.com", "password123");

        String registerJson = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regReq)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andReturn().getResponse().getContentAsString();

        assertTrue(registerJson.contains("token"));

        // Login with registered credentials
        LoginRequest loginReq = new LoginRequest("inttest_user", "password123");

        String loginJson = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.user.username").value("inttest_user"))
            .andReturn().getResponse().getContentAsString();

        assertTrue(loginJson.contains("token"));

        // Verify password is BCrypt-encoded in DB
        User saved = userRepository.findByUsername("inttest_user").orElseThrow();
        assertTrue(saved.getPassword().startsWith("$2a$"), "Password should be BCrypt-encoded");
    }

    @Test
    void shouldRegisterViaUserEndpointAndVerify() throws Exception {
        UserCreateRequest req = new UserCreateRequest("inttest_user2", "inttest2@test.com", "password456");

        String responseJson = mockMvc.perform(post("/api/users")
                .with(TestAuth.admin(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andReturn().getResponse().getContentAsString();

        assertTrue(responseJson.contains("inttest_user2"));

        // Verify via GET
        mockMvc.perform(get("/api/users/username/inttest_user2").with(TestAuth.admin(1L)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("inttest_user2"));
    }
}
