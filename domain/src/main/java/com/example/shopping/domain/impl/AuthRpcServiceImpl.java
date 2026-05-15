package com.example.shopping.domain.impl;

import com.example.shopping.common.dto.UserRequest;
import com.example.shopping.common.dto.UserResponse;
import com.example.shopping.common.security.TokenProvider;
import com.example.shopping.domain.mapper.UserMapper;
import com.example.shopping.facade.AuthRpcService;
import com.example.shopping.facade.dto.UserCreateRequest;
import com.example.shopping.facade.dto.UserDTO;
import com.example.shopping.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthRpcServiceImpl implements AuthRpcService {

    private static final Logger logger = LoggerFactory.getLogger(AuthRpcServiceImpl.class);

    private final UserService userService;
    private final UserMapper userMapper;
    private final TokenProvider tokenProvider;

    public AuthRpcServiceImpl(UserService userService, UserMapper userMapper,
                              TokenProvider tokenProvider) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public String login(String username, String password) {
        Long userId = userService.verifyCredentials(username, password);
        String token = tokenProvider.createToken(userId, username);
        logger.info("[RPC] login - user: {}", username);
        return token;
    }

    @Override
    public String register(UserCreateRequest request) {
        UserRequest userRequest = new UserRequest(request.username(), request.email(), request.password());
        UserResponse response = userService.createUser(userRequest);
        String token = tokenProvider.createToken(response.id(), response.username());
        logger.info("[RPC] register - user: {}", response.username());
        return token;
    }

    @Override
    public UserDTO getCurrentUser(Long userId) {
        UserResponse response = userService.getUserById(userId);
        return userMapper.toDTO(response);
    }
}
