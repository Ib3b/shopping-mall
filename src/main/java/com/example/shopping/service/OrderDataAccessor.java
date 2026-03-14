package com.example.shopping.service;

import com.example.shopping.entity.Order;
import com.example.shopping.entity.Product;
import com.example.shopping.entity.User;
import com.example.shopping.exception.BusinessException;
import com.example.shopping.repository.OrderRepository;
import com.example.shopping.repository.ProductRepository;
import com.example.shopping.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单数据访问服务
 * <p>
 * 封装订单相关的数据访问逻辑，包括用户、商品、订单的查询操作。
 * 用于简化 OrderService 的构造器注入参数。
 * </p>
 */
@Service
public class OrderDataAccessor {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderDataAccessor(OrderRepository orderRepository,
                             ProductRepository productRepository,
                             UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * 根据ID获取用户实体
     *
     * @param userId 用户ID
     * @return 用户实体
     * @throws BusinessException 当用户不存在时抛出
     */
    public User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("用户不存在"));
    }

    /**
     * 根据ID获取商品实体
     *
     * @param productId 商品ID
     * @return 商品实体
     * @throws BusinessException 当商品不存在时抛出
     */
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new BusinessException("商品不存在"));
    }

    /**
     * 根据ID获取订单实体
     *
     * @param orderId 订单ID
     * @return 订单实体
     * @throws BusinessException 当订单不存在时抛出
     */
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException("订单不存在"));
    }

    /**
     * 保存订单
     *
     * @param order 订单实体
     * @return 保存后的订单实体
     */
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    /**
     * 根据用户ID获取订单列表（按时间降序）
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findUserOrdersOrderByTime(userId);
    }

    /**
     * 根据状态获取订单列表
     *
     * @param status 订单状态
     * @return 订单列表
     */
    public List<Order> getOrdersByStatus(Order.Status status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * 分页获取所有订单
     *
     * @param pageable 分页参数
     * @return 订单分页
     */
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    /**
     * 获取所有订单
     *
     * @return 订单列表
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * 获取用户订单数量
     *
     * @param userId 用户ID
     * @return 订单数量
     */
    public Long countByUserId(Long userId) {
        return orderRepository.countByUserId(userId);
    }
}