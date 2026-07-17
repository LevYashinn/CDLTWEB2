package com.fashionstore.product.controller;

import com.fashionstore.product.entity.Category;
import com.fashionstore.product.exception.UnauthorizedException;
import com.fashionstore.product.service.CategoryBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryBrandService service;

    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(service.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<Category> create(@RequestHeader("X-User-Role") String role, @RequestBody Category category) {
        requireAdmin(role);
        return ResponseEntity.ok(service.createCategory(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable Long id, @RequestHeader("X-User-Role") String role,
                                            @RequestBody Category category) {
        requireAdmin(role);
        return ResponseEntity.ok(service.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestHeader("X-User-Role") String role) {
        requireAdmin(role);
        service.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    private void requireAdmin(String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) throw new UnauthorizedException("Chỉ ADMIN mới có quyền thực hiện thao tác này");
    }
}
