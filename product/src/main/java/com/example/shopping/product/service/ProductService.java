package com.example.shopping.product.service;

import com.example.shopping.common.dto.ProductRequest;
import com.example.shopping.common.dto.ProductResponse;
import com.example.shopping.common.entity.Product;
import com.example.shopping.common.exception.BusinessException;
import com.example.shopping.common.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品服务类
 * <p>
 * 提供商品的创建、查询、更新、删除等业务逻辑。
 * 支持缓存机制提高查询性能。
 * </p>
 */
@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 创建商品
     *
     * @param request 商品请求
     * @return 商品响应
     */
    @Transactional
    @CacheEvict(value = "productCache", allEntries = true)
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product(
            request.name(),
            request.description(),
            request.price(),
            request.stock(),
            request.category()
        );

        Product saved = productRepository.save(product);
        logger.info("创建商品: {}", saved.getName());

        return toResponse(saved);
    }

    /**
     * 根据ID获取商品
     *
     * @param id 商品ID
     * @return 商品响应
     * @throws BusinessException 当商品不存在时抛出
     */
    @Cacheable(value = "productCache", key = "#id")
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new BusinessException("商品不存在"));
        return toResponse(product);
    }

    /**
     * 分页获取所有商品
     *
     * @param pageable 分页参数
     * @return 商品分页响应
     */
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
            .map(this::toResponse);
    }

    /**
     * 获取所有商品（不分页，用于内部调用）
     *
     * @return 商品列表
     */
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * 根据分类获取商品
     *
     * @param category 商品分类
     * @return 商品列表
     */
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * 根据分类获取商品（带缓存）
     *
     * @param category 商品分类
     * @return 商品列表
     */
    @Cacheable(value = "productCache", key = "'category:' + #category")
    public List<ProductResponse> getProductsByCategoryCached(String category) {
        logger.debug("查询分类商品（未命中缓存）: {}", category);
        return productRepository.findByCategory(category).stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * 搜索商品
     *
     * @param keyword 关键词
     * @return 商品列表
     */
    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchByKeyword(keyword).stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * 获取有库存的商品
     *
     * @return 商品列表
     */
    public List<ProductResponse> getInStockProducts() {
        return productRepository.findInStockProducts().stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * 更新商品
     *
     * @param id      商品ID
     * @param request 商品请求
     * @return 商品响应
     * @throws BusinessException 当商品不存在时抛出
     */
    @Transactional
    @CacheEvict(value = "productCache", allEntries = true)
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new BusinessException("商品不存在"));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(request.category());

        Product saved = productRepository.save(product);
        logger.info("更新商品: {}", saved.getName());

        return toResponse(saved);
    }

    /**
     * 删除商品
     *
     * @param id 商品ID
     * @throws BusinessException 当商品不存在时抛出
     */
    @Transactional
    @CacheEvict(value = "productCache", allEntries = true)
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new BusinessException("商品不存在");
        }
        productRepository.deleteById(id);
        logger.info("删除商品ID: {}", id);
    }

    /**
     * 更新库存
     * <p>
     * 扣减库存（quantity为正数）或增加库存（quantity为负数）
     * </p>
     *
     * @param id       商品ID
     * @param quantity 数量变化（正数扣减，负数增加）
     * @return 商品响应
     * @throws BusinessException 当商品不存在或库存不足时抛出
     */
    @Transactional
    @CachePut(value = "productCache", key = "#id")
    public ProductResponse updateStock(Long id, int quantity) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new BusinessException("商品不存在"));

        int newStock = product.getStock() - quantity;
        if (newStock < 0) {
            throw new BusinessException("库存不足");
        }

        product.setStock(newStock);

        Product saved = productRepository.save(product);
        logger.info("扣减库存 - 商品: {}, 数量: {}, 剩余: {}",
            saved.getName(), quantity, saved.getStock());

        return toResponse(saved);
    }

    /**
     * 将商品实体转换为响应DTO
     *
     * @param product 商品实体
     * @return 商品响应DTO
     */
    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.getCategory(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}