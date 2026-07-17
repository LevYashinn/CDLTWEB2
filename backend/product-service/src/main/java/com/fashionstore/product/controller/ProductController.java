package com.fashionstore.product.controller;

import com.fashionstore.product.dto.*;
import com.fashionstore.product.exception.UnauthorizedException;
import com.fashionstore.product.service.FileStorageService;
import com.fashionstore.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(@RequestParam(required = false) String keyword,
                                                            @RequestParam(required = false) Long categoryId) {
        if (keyword != null && !keyword.isBlank()) {
            return ResponseEntity.ok(productService.search(keyword));
        }
        if (categoryId != null) {
            return ResponseEntity.ok(productService.getByCategory(categoryId));
        }
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestHeader("X-User-Role") String role,
                                                     @Valid @RequestBody ProductRequest request) {
        requireAdmin(role);
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                     @RequestHeader("X-User-Role") String role,
                                                     @Valid @RequestBody ProductRequest request) {
        requireAdmin(role);
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, @RequestHeader("X-User-Role") String role) {
        requireAdmin(role);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Internal endpoints consumed by Order Service via OpenFeign ----

    @GetMapping("/{id}/check-stock")
    public ResponseEntity<StockCheckResponse> checkStock(@PathVariable Long id, @RequestParam int quantity) {
        return ResponseEntity.ok(productService.checkStock(id, quantity));
    }

    @PutMapping("/stock")
    public ResponseEntity<Void> updateStock(@RequestBody StockUpdateRequest request) {
        productService.updateStock(request);
        return ResponseEntity.ok().build();
    }

    /** Admin - upload ảnh cho sản phẩm, trả về URL để lưu vào field imageUrl. */
    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> upload(@RequestHeader("X-User-Role") String role,
                                                  @RequestParam("file") MultipartFile file) {
        requireAdmin(role);
        String url = fileStorageService.store(file);
        return ResponseEntity.ok(new UploadResponse(url));
    }

    private void requireAdmin(String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new UnauthorizedException("Chỉ ADMIN mới có quyền quản lý sản phẩm");
        }
    }
}
