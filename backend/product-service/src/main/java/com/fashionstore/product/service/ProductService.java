package com.fashionstore.product.service;

import com.fashionstore.product.dto.*;
import com.fashionstore.product.entity.Brand;
import com.fashionstore.product.entity.Product;
import com.fashionstore.product.entity.Category;
import com.fashionstore.product.exception.ResourceNotFoundException;
import com.fashionstore.product.repository.BrandRepository;
import com.fashionstore.product.repository.ProductRepository;
import com.fashionstore.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id = " + id));
        return toResponse(product);
    }

    public List<ProductResponse> search(String keyword) {
        return productRepository.search(keyword).stream().map(this::toResponse).toList();
    }

    public List<ProductResponse> getByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .sku(request.getSku())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .imageUrl(request.getImageUrl())
                .sizes(request.getSizes())
                .colors(request.getColors())
                .material(request.getMaterial())
                .gender(request.getGender())
                .category(request.getCategoryId() != null ? findCategory(request.getCategoryId()) : null)
                .brand(request.getBrandId() != null ? findBrand(request.getBrandId()) : null)
                .build();
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id = " + id));

        product.setName(request.getName());
        product.setSku(request.getSku());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        if (request.getStock() != null) product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setSizes(request.getSizes());
        product.setColors(request.getColors());
        product.setMaterial(request.getMaterial());
        product.setGender(request.getGender());
        if (request.getCategoryId() != null) product.setCategory(findCategory(request.getCategoryId()));
        if (request.getBrandId() != null) product.setBrand(findBrand(request.getBrandId()));

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm với id = " + id);
        }
        productRepository.deleteById(id);
    }

    // ---- Used internally by Order Service via OpenFeign ----

    public StockCheckResponse checkStock(Long productId, int requestedQty) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id = " + productId));
        boolean available = product.getStock() >= requestedQty;
        return StockCheckResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .availableStock(product.getStock())
                .available(available)
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .build();
    }

    @Transactional
    public void updateStock(StockUpdateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với id = " + request.getProductId()));
        int newStock = product.getStock() + request.getQuantity();
        product.setStock(Math.max(newStock, 0));
        productRepository.save(product);
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với id = " + id));
    }

    private Brand findBrand(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu với id = " + id));
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .sizes(product.getSizes())
                .colors(product.getColors())
                .material(product.getMaterial())
                .gender(product.getGender())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .build();
    }
}
