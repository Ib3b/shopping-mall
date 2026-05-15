package com.example.shopping.user.service;

import com.example.shopping.common.dto.UserRequest;
import com.example.shopping.common.dto.UserResponse;
import com.example.shopping.common.entity.User;
import com.example.shopping.common.exception.BusinessException;
import com.example.shopping.common.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务类
 * <p>
 * 提供用户的创建、查询、更新、删除等业务逻辑。
 * 支持缓存机制提高查询性能。
 * </p>
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 创建用户
     *
     * @param request 用户请求
     * @return 用户响应
     * @throws BusinessException 当用户名或邮箱已存在时抛出
     */
    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("用户名已存在");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("邮箱已被注册");
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = new User(request.username(), request.email(), encodedPassword);
        User saved = userRepository.save(user);
        logger.info("创建新用户: {}", saved.getUsername());

        return toResponse(saved);
    }

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户响应
     * @throws BusinessException 当用户不存在时抛出
     */
    @Cacheable(value = "userCache", key = "#id")
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException("用户不存在"));
        return toResponse(user);
    }

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户响应
     * @throws BusinessException 当用户不存在时抛出
     */
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException("用户不存在"));
        return toResponse(user);
    }

    /**
     * 分页获取所有用户
     *
     * @param pageable 分页参数
     * @return 用户分页响应
     */
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(this::toResponse);
    }

    /**
     * 获取所有用户（不分页，用于内部调用）
     *
     * @return 用户列表
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * 更新用户
     *
     * @param id      用户ID
     * @param request 用户请求
     * @return 用户响应
     * @throws BusinessException 当用户不存在或用户名已被占用时抛出
     */
    @Transactional
    @CacheEvict(value = "userCache", key = "#id")
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException("用户不存在"));

        if (!user.getUsername().equals(request.username())
            && userRepository.existsByUsername(request.username())) {
            throw new BusinessException("用户名已存在");
        }

        user.setUsername(request.username());
        user.setEmail(request.email());
        if (request.password() != null && !request.password().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        User saved = userRepository.save(user);
        logger.info("更新用户: {}", saved.getUsername());

        return toResponse(saved);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @throws BusinessException 当用户不存在时抛出
     */
    @Transactional
    @CacheEvict(value = "userCache", key = "#id")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("用户不存在");
        }
        userRepository.deleteById(id);
        logger.info("删除用户ID: {}", id);
    }

    /**
     * 验证用户凭证
     *
     * @param username 用户名
     * @param rawPassword 明文密码
     * @return 用户ID
     * @throws BusinessException 当用户不存在或密码不匹配时抛出
     */
    public Long verifyCredentials(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new BusinessException("用户名或密码错误"));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        return user.getId();
    }

    /**
     * 将用户实体转换为响应DTO
     *
     * @param user 用户实体
     * @return 用户响应DTO
     */
    private UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreatedAt()
        );
    }
}