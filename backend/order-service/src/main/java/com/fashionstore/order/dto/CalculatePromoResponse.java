package com.fashionstore.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculatePromoResponse {
    private BigDecimal subtotal;
    private BigDecimal autoDiscount;
    private BigDecimal couponDiscount;
    private BigDecimal total;
    private boolean couponApplied;
    private String message;
}
