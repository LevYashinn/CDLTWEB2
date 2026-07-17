package com.fashionstore.cart.dto;

import lombok.Data;

@Data
public class UpdateCartRequest {
    private Long productId;
    private Integer quantity;
}
