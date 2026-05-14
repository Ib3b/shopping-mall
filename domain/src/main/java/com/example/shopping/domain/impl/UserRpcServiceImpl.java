package com.example.shopping.domain.impl;

import com.example.shopping.common.dto.UserRequest;
import com.example.shopping.common.dto.UserResponse;
import com.example.shopping.domain.mapper.UserMapper;
import com.example.shopping.facade.UserRpcService;
import com.example.shopping.facade.dto.UserCreateRequest;
import com.example.shopping.facade.dto.UserDTO;
import com.example.shopping.facade.dto.PageDTO;
import com.example.shopping.facade.dto.UserUpdateRequest;
import com.example.shopping.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户 RPC 服务实现
 * <p>
 * 实现 {@link UserRpcService} 接口，提供用户相关的 RPC 服务。
 * 作为 facade 层接口与 domain 层服务之间的适配器，负责 DTO 转换。
 * </p>
 */
@Service
public class UserRpcServiceImpl implements UserRpcService {

    private static final Logger logger = LoggerFactory.getLogger(UserRpcServiceImpl.class);

    private final UserService userService;
    private final UserMapper userMapper;

    public UserRpcServiceImpl(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDTO createUser(UserCreateRequest request) {
        logger.info("[RPC] createUser - username: {}", request.username());
        UserRequest userRequest = new UserRequest(request.username(), request.email(), request.password());
        UserResponse response = userService.createUser(userRequest);
        return userMapper.toDTO(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDTO getUserById(Long id) {
        logger.info("[RPC] getUserById - id: {}", id);
        UserResponse response = userService.getUserById(id);
        return userMapper.toDTO(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDTO getUserByUsername(String username) {
        logger.info("[RPC] getUserByUsername - username: {}", username);
        UserResponse response = userService.getUserByUsername(username);
        return userMapper.toDTO(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserDTO> getAllUsers() {
        logger.info("[RPC] getAllUsers");
        return userService.getAllUsers().stream()
            .map(userMapper::toDTO)
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageDTO<UserDTO> getAllUsers(int pageNumber, int pageSize) {
        logger.info("[RPC] getAllUsers (paged) - page: {}, size: {}", pageNumber, pageSize);
        Page<UserResponse> page = userService.getAllUsers(PageRequest.of(pageNumber, pageSize));
        List<UserDTO> content = page.getContent().stream()
            .map(userMapper::toDTO)
            .toList();
        return new PageDTO<>(
            content,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(Long id) {
        logger.info("[RPC] deleteUser - id: {}", id);
        userService.deleteUser(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDTO updateUser(Long id, UserUpdateRequest request) {
        logger.info("[RPC] updateUser - id: {}", id);
        UserRequest userRequest = new UserRequest(request.username(), request.email(), request.password());
        UserResponse response = userService.updateUser(id, userRequest);
        return userMapper.toDTO(response);
    }

}