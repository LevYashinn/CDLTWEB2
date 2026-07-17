package com.fashionstore.promotion.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDto {
    private Long productId;
    private Long categoryId;
    private BigDecimal price;
    private Integer quantity;
}
