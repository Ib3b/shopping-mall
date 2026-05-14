package com.example.shopping.order.service;

import com.example.shopping.common.dto.OrderRequest;
import com.example.shopping.common.dto.OrderResponse;
import com.example.shopping.common.entity.Order;
import com.example.shopping.common.entity.Product;
import com.example.shopping.common.entity.User;
import com.example.shopping.common.exception.BusinessException;
import com.example.shopping.order.event.OrderCreatedEvent;
import com.example.shopping.order.event.OrderStatusChangedEvent;
import com.example.shopping.order.state.OrderStateHandlerRegistry;
import com.example.shopping.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 订单服务类
 * <p>
 * 提供订单的创建、查询、状态管理等业务逻辑。
 * 使用状态模式管理状态转换，使用事件驱动处理通知。
 * </p>
 *
 * <h2>并发控制方案（库存扣减场景）</h2>
 *
 * 下单即扣库存是高并发场景下的典型竞争条件。以下按推荐优先级列出三种方案：
 *
 * <h3>方案一（当前实现 — 推荐最佳实践）：乐观锁 + 自动重试</h3>
 *
 * Product 和 Order 实体均包含 {@code @Version} 字段。
 * 当两个事务同时读取同一个 Product 并尝试修改时，第一个提交成功，第二个提交时
 * Hibernate 检测到版本不匹配 → 抛出 OptimisticLockException。
 * OrderService 在外层捕获此异常并自动重试整个事务（最多 3 次）。
 *
 * <pre>{@code
 * Product p1 = repo.findById(1L);  // version=1, stock=10
 * // 线程 B 同时执行: p2 也是 version=1, stock=10
 * p1.setStock(8);                  // A 提交: UPDATE WHERE version=1 → OK, version→2
 * p2.setStock(5);                  // B 提交: UPDATE WHERE version=1 → 0 rows, 抛出异常
 * // B 回滚并重试：重新读取 version=2, stock=8 → 扣 5 得 3, 提交成功
 * }</pre>
 *
 * <ul>
 *   <li>✓ 读操作无锁，低冲突下几乎零开销</li>
 *   <li>✓ JPA 原生支持，无需额外依赖</li>
 *   <li>✓ 配合 {@code TransactionTemplate} 保证重试时开启全新事务</li>
 *   <li>✗ 高冲突场景下重试次数多，响应时间增加</li>
 *   <li><b>适用场景：</b>大多数电商业务（冲突概率 &lt; 10%）</li>
 * </ul>
 *
 * <h3>方案二（备选）：悲观锁 —— SELECT ... FOR UPDATE</h3>
 *
 * 在读取商品时使用 {@code @Lock(PESSIMISTIC_WRITE)} 锁定该行，
 * 其他事务必须等待当前事务结束后才能读取或修改。
 *
 * <ul>
 *   <li>✓ 无需重试逻辑，等待即解决冲突</li>
 *   <li>✗ 读操作也要加锁，低冲突场景下不必要地降低吞吐</li>
 *   <li>✗ SQLite 锁机制有限（仅支持表级锁），需 PostgreSQL/MySQL</li>
 *   <li>✗ 连接持有时间长，连接池压力大</li>
 *   <li><b>适用场景：</b>秒杀等超高冲突、短事务场景</li>
 * </ul>
 *
 * <h3>方案三（备选）：原子 UPDATE —— 单条 SQL 扣减</h3>
 *
 * 使用 {@code @Modifying @Query} 执行原子 UPDATE 语句，在数据库层面完成
 * "检查库存 ≥ 数量"和"扣减"两个操作，通过影响行数判断结果。
 *
 * <ul>
 *   <li>✓ 吞吐最高，无事务冲突</li>
 *   <li>✓ 一条 SQL 完成「检查 + 扣减」，天然原子</li>
 *   <li>✗ 绕过 JPA {@code @Version} 和生命周期回调（如 {@code @PreUpdate}）</li>
 *   <li>✗ 需要手动 evict 缓存，缓存一致性难以保证</li>
 *   <li><b>适用场景：</b>纯库存扣减，无复杂业务逻辑</li>
 * </ul>
 *
 * <h3>方案对比总结</h3>
 * <pre>
 * ┌──────────────┬──────────────┬──────────────┬──────────────┐
 * │   维度       │ 乐观锁+重试  │   悲观锁     │  原子 UPDATE │
 * ├──────────────┼──────────────┼──────────────┼──────────────┤
 * │ 读性能       │ ★★★★★ 无锁  │ ★★★ 加锁    │ ★★★★★ 无锁   │
 * │ 写吞吐       │ ★★★★ 重试   │ ★★★ 排队    │ ★★★★★ 最高   │
 * │ 实现复杂度   │ ★★★★★ 简单  │ ★★★★ 简单   │ ★★★ 缓存麻烦 │
 * │ 通用性       │ ★★★★★ 通用  │ ★★★ DB依赖   │ ★★★ 仅数字    │
 * │ 推荐场景     │ 通用业务     │ 秒杀        │ 纯库存扣减   │
 * └──────────────┴──────────────┴──────────────┴──────────────┘
 * </pre>
 */
@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    /** 乐观锁冲突最大重试次数 */
    private static final int MAX_RETRIES = 3;

    /** 重试间隔基数（毫秒），第 n 次重试等待 baseMs × (2^n) */
    private static final long RETRY_BASE_MS = 50;

    private final OrderDataAccessor orderDataAccessor;
    private final ProductService productService;
    private final OrderStateHandlerRegistry stateHandlerRegistry;
    private final ApplicationEventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;

    public OrderService(OrderDataAccessor orderDataAccessor,
                        ProductService productService,
                        OrderStateHandlerRegistry stateHandlerRegistry,
                        ApplicationEventPublisher eventPublisher,
                        PlatformTransactionManager transactionManager) {
        this.orderDataAccessor = orderDataAccessor;
        this.productService = productService;
        this.stateHandlerRegistry = stateHandlerRegistry;
        this.eventPublisher = eventPublisher;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(
            TransactionDefinition.PROPAGATION_REQUIRED);
    }

    // ============================================================
    // 公共 API — 非事务入口，内嵌重试逻辑
    // ============================================================

    /**
     * 创建订单（支持乐观锁冲突自动重试）
     * <p>
     * 创建订单时会自动扣减库存并发布订单创建事件。
     * 当乐观锁冲突触发 {@link OptimisticLockingFailureException} 时，
     * 自动重试整个业务操作（含事务）最多 {@value #MAX_RETRIES} 次。
     * </p>
     *
     * @param request 订单请求
     * @return 订单响应
     * @throws BusinessException 当重试耗尽或用户/商品不存在/库存不足时抛出
     */
    public OrderResponse createOrder(OrderRequest request) {
        return retryOnOptimisticLocking(() ->
            transactionTemplate.execute(status -> createOrderInternal(request)));
    }

    /**
     * 更新订单状态（支持乐观锁冲突自动重试）
     * <p>
     * 使用状态模式验证状态转换的合法性，转换成功后发布状态变更事件。
     * 取消订单时会自动恢复库存。
     * </p>
     *
     * @param orderId   订单ID
     * @param newStatus 新状态
     * @return 订单响应
     * @throws BusinessException 当重试耗尽、订单不存在或状态转换不合法时抛出
     */
    public OrderResponse updateOrderStatus(Long orderId, Order.Status newStatus) {
        return retryOnOptimisticLocking(() ->
            transactionTemplate.execute(status -> updateOrderStatusInternal(orderId, newStatus)));
    }

    /**
     * 取消订单（支持乐观锁冲突自动重试）
     *
     * @param orderId 订单ID
     * @throws BusinessException 当重试耗尽、订单不存在或无法取消时抛出
     */
    public void cancelOrder(Long orderId) {
        retryOnOptimisticLocking(() -> {
            transactionTemplate.execute(status -> {
                updateOrderStatusInternal(orderId, Order.Status.CANCELLED);
                return null;
            });
            return null;
        });
    }

    // ============================================================
    // 查询接口（读操作无需事务重试）
    // ============================================================

    public OrderResponse getOrderById(Long id) {
        Order order = orderDataAccessor.getOrder(id);
        return toResponse(order);
    }

    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderDataAccessor.getOrdersByUserId(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    public List<OrderResponse> getOrdersByStatus(Order.Status status) {
        return orderDataAccessor.getOrdersByStatus(status).stream()
            .map(this::toResponse)
            .toList();
    }

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderDataAccessor.getAllOrders(pageable)
            .map(this::toResponse);
    }

    public List<OrderResponse> getAllOrders() {
        return orderDataAccessor.getAllOrders().stream()
            .map(this::toResponse)
            .toList();
    }

    public Long getUserOrderCount(Long userId) {
        return orderDataAccessor.countByUserId(userId);
    }

    // ============================================================
    // 内部写逻辑（在 TransactionTemplate 内执行，不直接标注 @Transactional）
    // ============================================================

    private OrderResponse createOrderInternal(OrderRequest request) {
        logger.info("创建订单 - 用户ID: {}, 商品ID: {}, 数量: {}",
            request.userId(), request.productId(), request.quantity());

        User user = orderDataAccessor.getUser(request.userId());
        Product product = orderDataAccessor.getProduct(request.productId());

        if (product.getStock() < request.quantity()) {
            throw new BusinessException("商品库存不足，当前库存: " + product.getStock());
        }

        Order order = new Order(user, product, request.quantity());
        Order saved = orderDataAccessor.saveOrder(order);

        productService.updateStock(request.productId(), request.quantity());

        eventPublisher.publishEvent(new OrderCreatedEvent(saved));

        logger.info("订单创建成功 - 订单ID: {}", saved.getId());

        return toResponse(saved);
    }

    private OrderResponse updateOrderStatusInternal(Long orderId, Order.Status newStatus) {
        Order order = orderDataAccessor.getOrder(orderId);
        Order.Status currentStatus = order.getStatus();

        if (!stateHandlerRegistry.canTransition(currentStatus, newStatus)) {
            throw new BusinessException("订单状态不允许从 " +
                currentStatus.getDescription() + " 变更为 " +
                newStatus.getDescription());
        }

        if (newStatus == Order.Status.CANCELLED) {
            productService.updateStock(order.getProduct().getId(), -order.getQuantity());
        }

        order.setStatus(newStatus);
        Order saved = orderDataAccessor.saveOrder(order);

        eventPublisher.publishEvent(new OrderStatusChangedEvent(saved, currentStatus, newStatus));

        logger.info("订单状态更新 - 订单ID: {}, 新状态: {}", orderId, newStatus);

        return toResponse(saved);
    }

    // ============================================================
    // 乐观锁重试工具
    // ============================================================

    /**
     * 执行操作并在遇到乐观锁冲突时自动重试。
     * <p>
     * 使用指数退避策略：第 n 次重试前等待 baseMs × 2^n 毫秒（n 从 0 开始）。
     * 超过最大重试次数后抛出 BusinessException。
     * </p>
     *
     * <h3>设计说明</h3>
     * <ul>
     *   <li>每次重试通过 {@link TransactionTemplate} 开启全新事务，保证事务隔离</li>
     *   <li>捕获 {@link OptimisticLockingFailureException}（Spring 统一包装，兼容
     *       Hibernate {@code StaleObjectStateException} 和 JPA {@code OptimisticLockException}）</li>
     *   <li>重试耗尽时抛出 {@link BusinessException}，避免底层异常泄漏到 Controller 层</li>
     * </ul>
     *
     * @param operation 需要重试的业务操作
     * @param <T>       返回值类型
     * @return 操作结果
     * @throws BusinessException 重试耗尽时抛出
     */
    private <T> T retryOnOptimisticLocking(Supplier<T> operation) {
        Throwable lastException = null;
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                return operation.get();
            } catch (OptimisticLockingFailureException e) {
                lastException = e;
                logger.warn("乐观锁冲突 (重试 {}/{})", i + 1, MAX_RETRIES);
                if (i < MAX_RETRIES - 1) {
                    sleep(RETRY_BASE_MS * (1L << i));
                }
            }
        }
        throw new BusinessException("系统繁忙，请稍后重试",
            Objects.requireNonNullElse(lastException, new RuntimeException()));
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ============================================================
    // DTO 转换
    // ============================================================

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getUser().getId(),
            order.getUser().getUsername(),
            order.getProduct().getId(),
            order.getProduct().getName(),
            order.getQuantity(),
            order.getTotalPrice(),
            order.getStatus().name(),
            order.getStatus().getDescription(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
}
