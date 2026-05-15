package com.example.shopping.web.controller;

import com.example.shopping.common.security.TokenProvider;
import com.example.shopping.facade.AuthRpcService;
import com.example.shopping.facade.dto.LoginRequest;
import com.example.shopping.facade.dto.LoginResponse;
import com.example.shopping.facade.dto.UserCreateRequest;
import com.example.shopping.facade.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、注册、当前用户查询")
public class AuthController {

    private final AuthRpcService authRpcService;
    private final TokenProvider tokenProvider;

    public AuthController(AuthRpcService authRpcService, TokenProvider tokenProvider) {
        this.authRpcService = authRpcService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "验证用户名密码并返回 JWT token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authRpcService.login(request.username(), request.password());
        Long userId = tokenProvider.getUserIdFromToken(token);
        UserDTO user = authRpcService.getCurrentUser(userId);
        return ResponseEntity.ok(new LoginResponse(token, user));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户并返回 JWT token")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody UserCreateRequest request) {
        String token = authRpcService.register(request);
        Long userId = tokenProvider.getUserIdFromToken(token);
        UserDTO user = authRpcService.getCurrentUser(userId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new LoginResponse(token, user));
    }

    @GetMapping("/me")
    @Operation(summary = "当前用户", description = "获取当前登录用户信息")
    public ResponseEntity<UserDTO> me(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserDTO user = authRpcService.getCurrentUser(userId);
        return ResponseEntity.ok(user);
    }
}
