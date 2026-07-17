package com.fashionstore.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /** Mã sản phẩm (SKU), dùng thay cho ISBN của sách trước đây. */
    private String sku;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Builder.Default
    private Integer stock = 0;

    private String imageUrl;

    /** Các size có sẵn, ví dụ: "S,M,L,XL". */
    private String sizes;

    /** Các màu có sẵn, ví dụ: "Đen,Trắng,Xanh navy". */
    private String colors;

    /** Chất liệu, ví dụ: "Cotton 100%", "Denim", "Len". */
    private String material;

    /** Đối tượng sử dụng: Nam, Nữ, Unisex, Trẻ em. */
    private String gender;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
