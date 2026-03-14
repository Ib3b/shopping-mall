package com.example.shopping.controller;

import com.example.shopping.dto.UserRequest;
import com.example.shopping.dto.UserResponse;
import com.example.shopping.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户控制器测试类
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void shouldCreateUser() throws Exception {
        UserRequest request = new UserRequest("newuser", "new@example.com", "password");
        UserResponse response = new UserResponse(1L, "newuser", "new@example.com", LocalDateTime.now());

        when(userService.createUser(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void shouldGetUserById() throws Exception {
        UserResponse response = new UserResponse(1L, "testuser", "test@example.com", LocalDateTime.now());

        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        UserResponse user1 = new UserResponse(1L, "user1", "user1@example.com", LocalDateTime.now());
        UserResponse user2 = new UserResponse(2L, "user2", "user2@example.com", LocalDateTime.now());

        Page<UserResponse> page = new PageImpl<>(List.of(user1, user2));
        when(userService.getAllUsers(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        when(userService.getUserById(999L)).thenThrow(new com.example.shopping.exception.BusinessException("用户不存在"));

        mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
            .andExpect(status().isNoContent());
    }
}