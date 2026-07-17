package com.fashionstore.order.dto;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long productId;
    private String imageUrl;
    private Integer quantity;
    private String size;
    private String color;
}
