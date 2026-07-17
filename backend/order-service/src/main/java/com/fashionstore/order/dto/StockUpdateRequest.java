package com.fashionstore.order.dto;

import lombok.Data;

@Data
public class StockUpdateRequest {
    private Long productId;
    private Integer quantity;
    public StockUpdateRequest() {}
    public StockUpdateRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
