package com.example.shopping.facade;

import com.example.shopping.facade.dto.ProductCreateRequest;
import com.example.shopping.facade.dto.ProductDTO;
import com.example.shopping.facade.dto.ProductUpdateRequest;

import java.util.List;

/**
 * 商品 RPC 服务接口
 * <p>
 * 对外提供的商品服务 RPC 接口定义。
 * 后续可对接 Dubbo、gRPC 等 RPC 框架。
 * </p>
 */
public interface ProductRpcService {

    /**
     * 创建商品
     *
     * @param request 商品请求
     * @return 商品响应
     */
    ProductDTO createProduct(ProductCreateRequest request);

    /**
     * 根据ID获取商品
     *
     * @param id 商品ID
     * @return 商品响应
     */
    ProductDTO getProductById(Long id);

    /**
     * 获取所有商品
     *
     * @return 商品列表
     */
    List<ProductDTO> getAllProducts();

    /**
     * 根据分类获取商品
     *
     * @param category 分类
     * @return 商品列表
     */
    List<ProductDTO> getProductsByCategory(String category);

    /**
     * 搜索商品
     *
     * @param keyword 关键词
     * @return 商品列表
     */
    List<ProductDTO> searchProducts(String keyword);

    /**
     * 获取有库存的商品
     *
     * @return 有库存的商品列表
     */
    List<ProductDTO> getInStockProducts();

    /**
     * 更新商品
     *
     * @param id 商品ID
     * @param request 商品更新请求
     * @return 商品响应
     */
    ProductDTO updateProduct(Long id, ProductUpdateRequest request);

    /**
     * 删除商品
     *
     * @param id 商品ID
     */
    void deleteProduct(Long id);
}