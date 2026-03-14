package com.example.shopping.controller;

import com.example.shopping.dto.ProductRequest;
import com.example.shopping.dto.ProductResponse;
import com.example.shopping.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 * <p>
 * 提供商品的 CRUD、库存管理等 REST API 接口。
 * </p>
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "商品管理", description = "商品CRUD、库存管理接口")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 创建商品
     *
     * @param request 商品请求
     * @return 创建的商品
     */
    @PostMapping
    @Operation(summary = "创建商品", description = "添加新商品")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 根据ID获取商品
     *
     * @param id 商品ID
     * @return 商品详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取商品", description = "根据ID获取商品详情")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 分页获取所有商品
     *
     * @param pageable 分页参数（默认每页10条）
     * @return 商品分页列表
     */
    @GetMapping
    @Operation(summary = "获取所有商品", description = "分页获取商品列表")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<ProductResponse> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * 根据分类查询商品
     *
     * @param category 分类名称
     * @return 商品列表
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "根据分类查询", description = "获取指定分类的商品列表")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @PathVariable String category) {
        List<ProductResponse> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    /**
     * 搜索商品
     *
     * @param keyword 关键词
     * @return 匹配的商品列表
     */
    @GetMapping("/search")
    @Operation(summary = "搜索商品", description = "根据关键词搜索商品")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @RequestParam String keyword) {
        List<ProductResponse> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }

    /**
     * 获取有库存的商品
     *
     * @return 有库存的商品列表
     */
    @GetMapping("/in-stock")
    @Operation(summary = "获取有库存商品", description = "获取所有有库存的商品")
    public ResponseEntity<List<ProductResponse>> getInStockProducts() {
        List<ProductResponse> products = productService.getInStockProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * 更新商品
     *
     * @param id      商品ID
     * @param request 商品请求
     * @return 更新后的商品
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新商品", description = "更新商品信息")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除商品
     *
     * @param id 商品ID
     * @return 无内容响应
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品", description = "根据ID删除商品")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}