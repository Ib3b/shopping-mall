package com.example.shopping.repository;

import com.example.shopping.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单数据访问接口
 * <p>
 * 提供订单实体的 CRUD 操作和自定义查询方法。
 * </p>
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 根据用户ID查找订单
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> findByUserId(Long userId);

    /**
     * 根据状态查找订单
     *
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> findByStatus(Order.Status status);

    /**
     * 根据用户ID查找订单并按时间降序排列
     *
     * @param userId 用户ID
     * @return 按时间降序排列的订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findUserOrdersOrderByTime(@Param("userId") Long userId);

    /**
     * 统计用户的订单数量
     *
     * @param userId 用户ID
     * @return 订单数量
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
}