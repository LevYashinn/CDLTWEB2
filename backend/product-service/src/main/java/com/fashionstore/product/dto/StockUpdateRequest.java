package com.fashionstore.product.dto;

import lombok.Data;

@Data
public class StockUpdateRequest {
    private Long productId;
    private Integer quantity; // negative to decrease stock, positive to restock (e.g. on cancel)
}
