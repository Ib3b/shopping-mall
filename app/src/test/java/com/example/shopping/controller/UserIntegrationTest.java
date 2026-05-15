package com.example.shopping.controller;

import com.example.shopping.common.entity.User;
import com.example.shopping.common.repository.UserRepository;
import com.example.shopping.facade.dto.LoginRequest;
import com.example.shopping.facade.dto.UserCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
    @WithMockUser
    void shouldRegisterViaUserEndpointAndVerify() throws Exception {
        UserCreateRequest req = new UserCreateRequest("inttest_user2", "inttest2@test.com", "password456");

        String responseJson = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andReturn().getResponse().getContentAsString();

        assertTrue(responseJson.contains("inttest_user2"));

        // Verify via GET
        mockMvc.perform(get("/api/users/username/inttest_user2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("inttest_user2"));
    }
}
