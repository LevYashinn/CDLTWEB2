package com.fashionstore.cart.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/** One line item stored as part of the "cart:{userId}" hash in Redis. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem implements Serializable {
    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private String imageUrl;
    private String size;
    private String color;
    /** Dùng để tính khuyến mãi tự động theo danh mục lúc xem trước giỏ hàng. */
    private Long categoryId;
    // 👇 THÊM DÒNG NÀY ĐỂ BÁO TỒN KHO LÊN FRONTEND 👇
    private Integer stock;
}
