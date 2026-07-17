package com.fashionstore.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    private String sku;
    private String description;

    @NotNull(message = "Giá sản phẩm không được để trống")
    private BigDecimal price;

    private Integer stock;
    private String imageUrl;

    /** Danh sách size, ví dụ: "S,M,L,XL" */
    private String sizes;

    /** Danh sách màu, ví dụ: "Đen,Trắng,Xanh navy" */
    private String colors;

    private String material;

    /** Nam, Nữ, Unisex, Trẻ em */
    private String gender;

    private Long categoryId;
    private Long brandId;
}
