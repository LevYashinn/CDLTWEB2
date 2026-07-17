package com.fashionstore.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Used by Order Service (via OpenFeign) to check stock/price before creating an order. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockCheckResponse {
    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer availableStock;
    private boolean available;
    /** Dùng để order-service áp khuyến mãi theo danh mục (promotion-service). */
    private Long categoryId;
}
