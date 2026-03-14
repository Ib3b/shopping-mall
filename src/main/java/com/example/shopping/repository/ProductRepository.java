package com.example.shopping.repository;

import com.example.shopping.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品数据访问接口
 * <p>
 * 提供商品实体的 CRUD 操作和自定义查询方法。
 * </p>
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
}