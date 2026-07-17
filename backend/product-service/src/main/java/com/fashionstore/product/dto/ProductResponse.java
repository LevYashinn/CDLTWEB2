package com.fashionstore.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String sku;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private String sizes;
    private String colors;
    private String material;
    private String gender;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
}
