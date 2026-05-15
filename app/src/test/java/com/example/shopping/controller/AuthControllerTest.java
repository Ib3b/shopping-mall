package com.example.shopping.controller;

import com.example.shopping.common.exception.BusinessException;
import com.example.shopping.common.security.TokenProvider;
import com.example.shopping.facade.AuthRpcService;
import com.example.shopping.facade.dto.LoginRequest;
import com.example.shopping.facade.dto.UserCreateRequest;
import com.example.shopping.facade.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthRpcService authRpcService;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Test
    void shouldReturnTokenOnLogin() throws Exception {
        when(authRpcService.login("testuser", "password123")).thenReturn("jwt-token-abc");
        when(tokenProvider.getUserIdFromToken("jwt-token-abc")).thenReturn(1L);
        when(authRpcService.getCurrentUser(1L))
            .thenReturn(new UserDTO(1L, "testuser", "test@example.com", LocalDateTime.now()));

        LoginRequest request = new LoginRequest("testuser", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt-token-abc"))
            .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    void shouldReturn401OnInvalidCredentials() throws Exception {
        when(authRpcService.login("wrong", "wrong"))
            .thenThrow(new BusinessException("用户名或密码错误"));

        LoginRequest request = new LoginRequest("wrong", "wrong");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnTokenOnRegister() throws Exception {
        when(authRpcService.register(any(UserCreateRequest.class))).thenReturn("jwt-token-xyz");
        when(tokenProvider.getUserIdFromToken("jwt-token-xyz")).thenReturn(2L);
        when(authRpcService.getCurrentUser(2L))
            .thenReturn(new UserDTO(2L, "newuser", "new@test.com", LocalDateTime.now()));

        UserCreateRequest request = new UserCreateRequest("newuser", "new@test.com", "password");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").value("jwt-token-xyz"))
            .andExpect(jsonPath("$.user.username").value("newuser"));
    }
}
