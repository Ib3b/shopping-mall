package com.example.shopping.domain.impl;

import com.example.shopping.common.dto.ProductRequest;
import com.example.shopping.common.dto.ProductResponse;
import com.example.shopping.facade.ProductRpcService;
import com.example.shopping.facade.dto.ProductCreateRequest;
import com.example.shopping.facade.dto.ProductDTO;
import com.example.shopping.facade.dto.ProductUpdateRequest;
import com.example.shopping.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品 RPC 服务实现
 */
@Service
public class ProductRpcServiceImpl implements ProductRpcService {

    private static final Logger logger = LoggerFactory.getLogger(ProductRpcServiceImpl.class);

    private final ProductService productService;

    public ProductRpcServiceImpl(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public ProductDTO createProduct(ProductCreateRequest request) {
        logger.info("[RPC] createProduct - name: {}", request.name());
        ProductRequest productRequest = new ProductRequest(
            request.name(), request.description(), request.price(), request.stock(), request.category()
        );
        ProductResponse response = productService.createProduct(productRequest);
        return toDTO(response);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        logger.info("[RPC] getProductById - id: {}", id);
        ProductResponse response = productService.getProductById(id);
        return toDTO(response);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        logger.info("[RPC] getAllProducts");
        return productService.getAllProducts().stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public List<ProductDTO> getProductsByCategory(String category) {
        logger.info("[RPC] getProductsByCategory - category: {}", category);
        return productService.getProductsByCategory(category).stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public List<ProductDTO> searchProducts(String keyword) {
        logger.info("[RPC] searchProducts - keyword: {}", keyword);
        return productService.searchProducts(keyword).stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public void deleteProduct(Long id) {
        logger.info("[RPC] deleteProduct - id: {}", id);
        productService.deleteProduct(id);
    }

    @Override
    public List<ProductDTO> getInStockProducts() {
        logger.info("[RPC] getInStockProducts");
        return productService.getInStockProducts().stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductUpdateRequest request) {
        logger.info("[RPC] updateProduct - id: {}", id);
        ProductRequest productRequest = new ProductRequest(
            request.name(), request.description(), request.price(), request.stock(), request.category()
        );
        ProductResponse response = productService.updateProduct(id, productRequest);
        return toDTO(response);
    }

    private ProductDTO toDTO(ProductResponse response) {
        return new ProductDTO(
            response.id(),
            response.name(),
            response.description(),
            response.price(),
            response.stock(),
            response.category(),
            response.createdAt(),
            response.updatedAt()
        );
    }
}