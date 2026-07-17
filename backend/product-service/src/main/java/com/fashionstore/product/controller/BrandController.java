package com.fashionstore.product.controller;

import com.fashionstore.product.entity.Brand;
import com.fashionstore.product.exception.UnauthorizedException;
import com.fashionstore.product.service.CategoryBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final CategoryBrandService service;

    @GetMapping
    public ResponseEntity<List<Brand>> getAll() {
        return ResponseEntity.ok(service.getAllBrands());
    }

    @PostMapping
    public ResponseEntity<Brand> create(@RequestHeader("X-User-Role") String role, @RequestBody Brand brand) {
        requireAdmin(role);
        return ResponseEntity.ok(service.createBrand(brand));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Brand> update(@PathVariable Long id, @RequestHeader("X-User-Role") String role,
                                         @RequestBody Brand brand) {
        requireAdmin(role);
        return ResponseEntity.ok(service.updateBrand(id, brand));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestHeader("X-User-Role") String role) {
        requireAdmin(role);
        service.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }

    private void requireAdmin(String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) throw new UnauthorizedException("Chỉ ADMIN mới có quyền thực hiện thao tác này");
    }
}
