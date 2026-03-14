package com.example.shopping.web.controller;

import com.example.shopping.common.dto.UserResponse;
import com.example.shopping.facade.UserRpcService;
import com.example.shopping.facade.dto.UserCreateRequest;
import com.example.shopping.facade.dto.UserDTO;
import com.example.shopping.facade.dto.UserUpdateRequest;
import com.example.shopping.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * <p>
 * 提供用户的注册、登录、查询等 REST API 接口。
 * </p>
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户注册、登录、查询接口")
public class UserController {

    private final UserRpcService userRpcService;
    private final UserService userService;

    public UserController(UserRpcService userRpcService, UserService userService) {
        this.userRpcService = userRpcService;
        this.userService = userService;
    }

    /**
     * 用户注册
     *
     * @param request 用户请求
     * @return 创建的用户
     */
    @PostMapping
    @Operation(summary = "用户注册", description = "创建新用户")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserDTO response = userRpcService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户", description = "根据ID获取用户信息")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO response = userRpcService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 分页获取所有用户
     *
     * @param pageable 分页参数（默认每页10条）
     * @return 用户分页列表
     */
    @GetMapping
    @Operation(summary = "获取所有用户", description = "分页获取用户列表")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/username/{username}")
    @Operation(summary = "根据用户名查询", description = "根据用户名获取用户信息")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO response = userRpcService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }

    /**
     * 更新用户
     *
     * @param id      用户ID
     * @param request 用户请求
     * @return 更新后的用户
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "更新用户信息")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserDTO response = userRpcService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 无内容响应
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "根据ID删除用户")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRpcService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}