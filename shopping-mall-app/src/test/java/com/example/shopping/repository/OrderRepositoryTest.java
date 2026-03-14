package com.example.shopping.repository;

import com.example.shopping.entity.Order;
import com.example.shopping.entity.Product;
import com.example.shopping.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 订单 Repository 测试类
 */
@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldSaveAndFindOrder() {
        User user = userRepository.save(new User("orderuser", "order@example.com", "password123"));
        Product product = productRepository.save(new Product("订单商品", "描述", new BigDecimal("100"), 10, "分类"));

        Order order = new Order(user, product, 2);
        Order saved = orderRepository.save(order);

        assertNotNull(saved.getId());
        assertEquals(Order.Status.PENDING, saved.getStatus());
        assertEquals(new BigDecimal("200"), saved.getTotalPrice());
    }

    @Test
    void shouldFindByUserId() {
        User user1 = userRepository.save(new User("user1", "u1@example.com", "password123"));
        User user2 = userRepository.save(new User("user2", "u2@example.com", "password456"));
        Product product = productRepository.save(new Product("商品", "描述", new BigDecimal("50"), 100, "分类"));

        orderRepository.save(new Order(user1, product, 1));
        orderRepository.save(new Order(user1, product, 2));
        orderRepository.save(new Order(user2, product, 3));

        List<Order> user1Orders = orderRepository.findByUserId(user1.getId());
        assertEquals(2, user1Orders.size());

        List<Order> user2Orders = orderRepository.findByUserId(user2.getId());
        assertEquals(1, user2Orders.size());
    }

    @Test
    void shouldFindByStatus() {
        User user = userRepository.save(new User("statususer", "status@example.com", "password123"));
        Product product = productRepository.save(new Product("商品", "描述", new BigDecimal("50"), 100, "分类"));

        Order order1 = new Order(user, product, 1);
        order1.setStatus(Order.Status.PAID);
        orderRepository.save(order1);

        Order order2 = new Order(user, product, 2);
        order2.setStatus(Order.Status.PENDING);
        orderRepository.save(order2);

        List<Order> paidOrders = orderRepository.findByStatus(Order.Status.PAID);
        assertEquals(1, paidOrders.size());

        List<Order> pendingOrders = orderRepository.findByStatus(Order.Status.PENDING);
        assertEquals(1, pendingOrders.size());
    }

    @Test
    void shouldCountByUserId() {
        User user = userRepository.save(new User("countuser", "count@example.com", "password123"));
        Product product = productRepository.save(new Product("商品", "描述", new BigDecimal("50"), 100, "分类"));

        orderRepository.save(new Order(user, product, 1));
        orderRepository.save(new Order(user, product, 2));

        Long count = orderRepository.countByUserId(user.getId());
        assertEquals(2L, count);
    }
}