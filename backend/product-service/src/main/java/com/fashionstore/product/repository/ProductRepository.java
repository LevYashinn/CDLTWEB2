package com.fashionstore.product.repository;

import com.fashionstore.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByBrandId(Long brandId);

    @Query("SELECT b FROM Product b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.brand.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> search(@Param("keyword") String keyword);
}
