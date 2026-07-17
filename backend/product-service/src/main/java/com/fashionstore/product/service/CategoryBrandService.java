package com.fashionstore.product.service;

import com.fashionstore.product.entity.Brand;
import com.fashionstore.product.entity.Category;
import com.fashionstore.product.exception.BadRequestException;
import com.fashionstore.product.exception.ResourceNotFoundException;
import com.fashionstore.product.repository.BrandRepository;
import com.fashionstore.product.repository.CategoryRepository;
import com.fashionstore.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryBrandService {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category payload) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id = " + id));
        category.setName(payload.getName());
        category.setDescription(payload.getDescription());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id = " + id));
        if (!productRepository.findByCategoryId(id).isEmpty()) {
            throw new BadRequestException("Không thể xóa danh mục \"" + category.getName()
                    + "\" vì vẫn còn sản phẩm thuộc danh mục này. Vui lòng chuyển hoặc xóa các sản phẩm đó trước.");
        }
        categoryRepository.deleteById(id);
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand createBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    public Brand updateBrand(Long id, Brand payload) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với id = " + id));
        brand.setName(payload.getName());
        brand.setDescription(payload.getDescription());
        return brandRepository.save(brand);
    }

    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với id = " + id));
        if (!productRepository.findByBrandId(id).isEmpty()) {
            throw new BadRequestException("Không thể xóa thương hiệu \"" + brand.getName()
                    + "\" vì vẫn còn sản phẩm thuộc thương hiệu này. Vui lòng chuyển hoặc xóa các sản phẩm đó trước.");
        }
        brandRepository.deleteById(id);
    }
}
