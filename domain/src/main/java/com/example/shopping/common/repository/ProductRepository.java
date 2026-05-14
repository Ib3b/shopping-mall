package com.example.shopping.common.repository;

import com.example.shopping.common.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品数据访问接口
 * <p>
 * 提供商品实体的 CRUD 操作和自定义查询方法。
 * 包含三种并发扣库存方案的 Repository 层支持：
 *
 * <pre>
 * 方案一（最佳实践）：乐观锁 + 重试（由 OrderService 实现）
 *   - Product 实体已含 {@code @Version} 字段
 *   - OrderService 在捕获 OptimisticLockException 后自动重试
 *
 * 方案二（可选）：悲观锁
 *   - 使用 {@code @Lock(PESSIMISTIC_WRITE)} 在读取时锁定行
 *   - 见下面的 findByProductWithPessimisticLock() 方法
 *
 * 方案三（可选）：原子 UPDATE
 *   - 使用单条 SQL 原子扣减库存
 *   - 见下面的 deductStockAtomic() 方法
 * </pre>
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 根据分类查找商品
     *
     * @param category 分类名称
     * @return 商品列表
     */
    List<Product> findByCategory(String category);

    /**
     * 查找有库存的商品
     *
     * @return 有库存的商品列表
     */
    @Query("SELECT p FROM Product p WHERE p.stock > 0")
    List<Product> findInStockProducts();

    /**
     * 根据关键词搜索商品
     * <p>
     * 在商品名称和描述中进行模糊匹配。
     * </p>
     *
     * @param keyword 关键词
     * @return 匹配的商品列表
     */
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 根据分类查找商品并按价格降序排列
     *
     * @param category 分类名称
     * @return 按价格降序排列的商品列表
     */
    List<Product> findByCategoryOrderByPriceDesc(String category);

    // ============================================================
    // 方案二：悲观锁 —— SELECT ... FOR UPDATE
    // ============================================================
    // 使用 PESSIMISTIC_WRITE 锁，在读取时直接锁定行直到事务结束。
    // 适合秒杀等高冲突场景，但会降低读并发且 SQLite 锁支持有限。
    // 使用方式（在 OrderService 中）：
    //
    //   Product product = productRepository.findProductWithLock(productId)
    //       .orElseThrow(() -> new BusinessException("商品不存在"));
    //   // 此时其他事务无法修改或读取（取决于 DB 隔离级别）该行
    //   if (product.getStock() < quantity) throw ...;
    //   product.setStock(product.getStock() - quantity);
    //
    // ✓ 无需重试，等待即解决
    // ✗ 读操作也要加锁，低冲突场景下不必要地降低吞吐
    // ✗ SQLite 锁机制有限，生产环境可考虑 PostgreSQL/MySQL
    //
    // @Lock(PESSIMISTIC_WRITE)
    // @Query("SELECT p FROM Product p WHERE p.id = :id")
    // Optional<Product> findProductWithLock(@Param("id") Long id);

    // ============================================================
    // 方案三：原子 UPDATE —— 单条 SQL 扣减 + 行数判断
    // ============================================================
    // 使用 UPDATE 语句在数据库层面原子扣减库存，通过影响行数判断是否成功。
    // 适合纯粹的数字扣减场景，性能最高。
    //
    // 使用方式（在 OrderService 中）：
    //
    //   int affected = productRepository.deductStockAtomic(productId, quantity);
    //   if (affected == 0) throw new BusinessException("库存不足");
    //   // 此时还需要手动 evict 缓存：
    //   cacheManager.getCache("productCache").evict(productId);
    //
    // ✓ 无事务冲突，吞吐最高
    // ✓ 检查+扣减一条 SQL 完成，天然原子
    // ✗ 绕过 @Version 和 JPA 生命周期回调
    // ✗ 需要手动管理缓存一致性
    //
    // @Modifying
    // @Query("UPDATE Product p SET p.stock = p.stock - :quantity " +
    //        "WHERE p.id = :id AND p.stock >= :quantity")
    // int deductStockAtomic(@Param("id") Long id, @Param("quantity") int quantity);
}