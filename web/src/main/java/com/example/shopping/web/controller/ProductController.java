package com.example.shopping.web.controller;

import com.example.shopping.facade.ProductRpcService;
import com.example.shopping.facade.dto.PageDTO;
import com.example.shopping.facade.dto.ProductCreateRequest;
import com.example.shopping.facade.dto.ProductDTO;
import com.example.shopping.facade.dto.ProductUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    private final ProductRpcService productRpcService;

    public ProductController(ProductRpcService productRpcService) {
        this.productRpcService = productRpcService;
    }

    /**
     * 创建商品
     *
     * @param request 商品请求
     * @return 创建的商品
     */
    @PostMapping
    @Operation(summary = "创建商品", description = "添加新商品")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductDTO response = productRpcService.createProduct(request);
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
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO response = productRpcService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 分页获取所有商品
     *
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认10）
     * @return 商品分页列表
     */
    @GetMapping
    @Operation(summary = "获取所有商品", description = "分页获取商品列表")
    public ResponseEntity<PageDTO<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageDTO<ProductDTO> products = productRpcService.getAllProducts(page, size);
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
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(
            @PathVariable String category) {
        List<ProductDTO> products = productRpcService.getProductsByCategory(category);
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
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @RequestParam String keyword) {
        List<ProductDTO> products = productRpcService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }

    /**
     * 获取有库存的商品
     *
     * @return 有库存的商品列表
     */
    @GetMapping("/in-stock")
    @Operation(summary = "获取有库存商品", description = "获取所有有库存的商品")
    public ResponseEntity<List<ProductDTO>> getInStockProducts() {
        List<ProductDTO> products = productRpcService.getInStockProducts();
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
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        ProductDTO response = productRpcService.updateProduct(id, request);
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
        productRpcService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}