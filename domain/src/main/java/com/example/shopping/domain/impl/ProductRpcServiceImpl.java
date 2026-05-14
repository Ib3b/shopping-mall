package com.example.shopping.domain.impl;

import com.example.shopping.common.dto.ProductRequest;
import com.example.shopping.common.dto.ProductResponse;
import com.example.shopping.domain.mapper.ProductMapper;
import com.example.shopping.facade.ProductRpcService;
import com.example.shopping.facade.dto.PageDTO;
import com.example.shopping.facade.dto.ProductCreateRequest;
import com.example.shopping.facade.dto.ProductDTO;
import com.example.shopping.facade.dto.ProductUpdateRequest;
import com.example.shopping.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品 RPC 服务实现
 * <p>
 * 实现 {@link ProductRpcService} 接口，提供商品相关的 RPC 服务。
 * 作为 facade 层接口与 domain 层服务之间的适配器，负责 DTO 转换。
 * </p>
 */
@Service
public class ProductRpcServiceImpl implements ProductRpcService {

    private static final Logger logger = LoggerFactory.getLogger(ProductRpcServiceImpl.class);

    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductRpcServiceImpl(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductDTO createProduct(ProductCreateRequest request) {
        logger.info("[RPC] createProduct - name: {}", request.name());
        ProductRequest productRequest = new ProductRequest(
            request.name(), request.description(), request.price(), request.stock(), request.category()
        );
        ProductResponse response = productService.createProduct(productRequest);
        return productMapper.toDTO(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductDTO getProductById(Long id) {
        logger.info("[RPC] getProductById - id: {}", id);
        ProductResponse response = productService.getProductById(id);
        return productMapper.toDTO(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductDTO> getAllProducts() {
        logger.info("[RPC] getAllProducts");
        return productService.getAllProducts().stream()
            .map(productMapper::toDTO)
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageDTO<ProductDTO> getAllProducts(int pageNumber, int pageSize) {
        logger.info("[RPC] getAllProducts (paged) - page: {}, size: {}", pageNumber, pageSize);
        Page<ProductResponse> page = productService.getAllProducts(PageRequest.of(pageNumber, pageSize));
        List<ProductDTO> content = page.getContent().stream()
            .map(productMapper::toDTO)
            .toList();
        return new PageDTO<>(
            content,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductDTO> getProductsByCategory(String category) {
        logger.info("[RPC] getProductsByCategory - category: {}", category);
        return productService.getProductsByCategory(category).stream()
            .map(productMapper::toDTO)
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductDTO> searchProducts(String keyword) {
        logger.info("[RPC] searchProducts - keyword: {}", keyword);
        return productService.searchProducts(keyword).stream()
            .map(productMapper::toDTO)
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProduct(Long id) {
        logger.info("[RPC] deleteProduct - id: {}", id);
        productService.deleteProduct(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ProductDTO> getInStockProducts() {
        logger.info("[RPC] getInStockProducts");
        return productService.getInStockProducts().stream()
            .map(productMapper::toDTO)
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductDTO updateProduct(Long id, ProductUpdateRequest request) {
        logger.info("[RPC] updateProduct - id: {}", id);
        ProductRequest productRequest = new ProductRequest(
            request.name(), request.description(), request.price(), request.stock(), request.category()
        );
        ProductResponse response = productService.updateProduct(id, productRequest);
        return productMapper.toDTO(response);
    }

}