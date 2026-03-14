package com.example.shopping.domain.impl;

import com.example.shopping.common.dto.UserRequest;
import com.example.shopping.common.dto.UserResponse;
import com.example.shopping.facade.UserRpcService;
import com.example.shopping.facade.dto.UserCreateRequest;
import com.example.shopping.facade.dto.UserDTO;
import com.example.shopping.facade.dto.UserUpdateRequest;
import com.example.shopping.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户 RPC 服务实现
 */
@Service
public class UserRpcServiceImpl implements UserRpcService {

    private static final Logger logger = LoggerFactory.getLogger(UserRpcServiceImpl.class);

    private final UserService userService;

    public UserRpcServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDTO createUser(UserCreateRequest request) {
        logger.info("[RPC] createUser - username: {}", request.username());
        UserRequest userRequest = new UserRequest(request.username(), request.email(), request.password());
        UserResponse response = userService.createUser(userRequest);
        return toDTO(response);
    }

    @Override
    public UserDTO getUserById(Long id) {
        logger.info("[RPC] getUserById - id: {}", id);
        UserResponse response = userService.getUserById(id);
        return toDTO(response);
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        logger.info("[RPC] getUserByUsername - username: {}", username);
        UserResponse response = userService.getUserByUsername(username);
        return toDTO(response);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        logger.info("[RPC] getAllUsers");
        return userService.getAllUsers().stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public void deleteUser(Long id) {
        logger.info("[RPC] deleteUser - id: {}", id);
        userService.deleteUser(id);
    }

    @Override
    public UserDTO updateUser(Long id, UserUpdateRequest request) {
        logger.info("[RPC] updateUser - id: {}", id);
        UserRequest userRequest = new UserRequest(request.username(), request.email(), request.password());
        UserResponse response = userService.updateUser(id, userRequest);
        return toDTO(response);
    }

    private UserDTO toDTO(UserResponse response) {
        return new UserDTO(
            response.id(),
            response.username(),
            response.email(),
            response.createdAt()
        );
    }
}