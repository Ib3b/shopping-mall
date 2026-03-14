package com.example.shopping.service;

import com.example.shopping.dto.ProductRequest;
import com.example.shopping.dto.ProductResponse;
import com.example.shopping.entity.Product;
import com.example.shopping.exception.BusinessException;
import com.example.shopping.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 商品服务测试类
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product("测试商品", "这是一个测试",
            new BigDecimal("99.99"), 100, "测试分类");
        testProduct.setId(1L);
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void shouldCreateProductSuccessfully() {
        ProductRequest request = new ProductRequest(
            "新商品", "描述", new BigDecimal("199.99"), 50, "新分类"
        );

        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        ProductResponse response = productService.createProduct(request);

        assertNotNull(response);
        assertEquals("新商品", response.name());
        assertEquals(new BigDecimal("199.99"), response.price());
    }

    @Test
    void shouldGetProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        ProductResponse response = productService.getProductById(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("测试商品", response.name());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> productService.getProductById(999L));
    }

    @Test
    void shouldGetAllProducts() {
        when(productRepository.findAll()).thenReturn(java.util.List.of(testProduct));

        var responses = productService.getAllProducts();

        assertEquals(1, responses.size());
        assertEquals("测试商品", responses.get(0).name());
    }

    @Test
    void shouldGetProductsByCategory() {
        when(productRepository.findByCategory("测试分类"))
            .thenReturn(java.util.List.of(testProduct));

        var responses = productService.getProductsByCategory("测试分类");

        assertEquals(1, responses.size());
        assertEquals("测试分类", responses.get(0).category());
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        ProductRequest request = new ProductRequest(
            "更新商品", "更新描述", new BigDecimal("199.99"), 50, "更新分类"
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.updateProduct(1L, request);

        assertNotNull(response);
        assertEquals("更新商品", response.name());
        assertEquals("更新描述", response.description());
    }

    @Test
    void shouldUpdateStockSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.updateStock(1L, 10);

        assertNotNull(response);
        assertEquals(90, response.stock());
    }

    @Test
    void shouldThrowExceptionWhenInsufficientStock() {
        testProduct.setStock(5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThrows(BusinessException.class, () -> productService.updateStock(1L, 10));
    }

    @Test
    void shouldDeleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentProduct() {
        when(productRepository.existsById(999L)).thenReturn(false);

        assertThrows(BusinessException.class, () -> productService.deleteProduct(999L));
    }

    @Test
    void shouldSearchProducts() {
        when(productRepository.searchByKeyword("测试"))
            .thenReturn(java.util.List.of(testProduct));

        var responses = productService.searchProducts("测试");

        assertEquals(1, responses.size());
    }
}