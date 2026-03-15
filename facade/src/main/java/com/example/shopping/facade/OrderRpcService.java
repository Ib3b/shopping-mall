package com.example.shopping.facade;

import com.example.shopping.facade.dto.OrderCreateRequest;
import com.example.shopping.facade.dto.OrderDTO;
import com.example.shopping.facade.dto.PageDTO;
import com.example.shopping.facade.enums.OrderStatus;

import java.util.List;

/**
 * 订单 RPC 服务接口
 * <p>
 * 对外提供的订单服务 RPC 接口定义。
 * 后续可对接 Dubbo、gRPC 等 RPC 框架。
 * </p>
 */
public interface OrderRpcService {

    /**
     * 创建订单
     *
     * @param request 订单请求
     * @return 订单响应
     */
    OrderDTO createOrder(OrderCreateRequest request);

    /**
     * 根据ID获取订单
     *
     * @param id 订单ID
     * @return 订单响应
     */
    OrderDTO getOrderById(Long id);

    /**
     * 获取所有订单
     *
     * @return 订单列表
     */
    List<OrderDTO> getAllOrders();

    /**
     * 分页获取所有订单
     *
     * @param pageNumber 页码（从0开始）
     * @param pageSize 每页大小
     * @return 分页订单列表
     */
    PageDTO<OrderDTO> getAllOrders(int pageNumber, int pageSize);

    /**
     * 根据用户ID获取订单
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<OrderDTO> getOrdersByUserId(Long userId);

    /**
     * 根据状态获取订单
     *
     * @param status 订单状态
     * @return 订单列表
     */
    List<OrderDTO> getOrdersByStatus(OrderStatus status);

    /**
     * 更新订单状态
     *
     * @param orderId   订单ID
     * @param newStatus 新状态
     * @return 订单响应
     */
    OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     */
    void cancelOrder(Long orderId);

    /**
     * 获取用户订单数量
     *
     * @param userId 用户ID
     * @return 订单数量
     */
    Long getUserOrderCount(Long userId);
}