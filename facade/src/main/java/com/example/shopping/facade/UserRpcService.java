package com.example.shopping.facade;

import com.example.shopping.facade.dto.UserCreateRequest;
import com.example.shopping.facade.dto.UserDTO;
import com.example.shopping.facade.dto.UserUpdateRequest;
import com.example.shopping.facade.dto.PageDTO;

import java.util.List;

/**
 * 用户 RPC 服务接口
 * <p>
 * 对外提供的用户服务 RPC 接口定义。
 * 后续可对接 Dubbo、gRPC 等 RPC 框架。
 * </p>
 */
public interface UserRpcService {

    /**
     * 创建用户
     *
     * @param request 用户请求
     * @return 用户响应
     */
    UserDTO createUser(UserCreateRequest request);

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户响应
     */
    UserDTO getUserById(Long id);

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户响应
     */
    UserDTO getUserByUsername(String username);

    /**
     * 获取所有用户
     *
     * @return 用户列表
     */
    List<UserDTO> getAllUsers();

    /**
     * 分页获取所有用户
     *
     * @param pageNumber 页码（从0开始）
     * @param pageSize 每页大小
     * @return 分页用户列表
     */
    PageDTO<UserDTO> getAllUsers(int pageNumber, int pageSize);

    /**
     * 更新用户
     *
     * @param id 用户ID
     * @param request 用户更新请求
     * @return 用户响应
     */
    UserDTO updateUser(Long id, UserUpdateRequest request);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void deleteUser(Long id);
}