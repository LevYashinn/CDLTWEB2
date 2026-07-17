package com.fashionstore.promotion.dto;

import lombok.Data;

import java.util.List;

@Data
public class CalculateCartRequest {
    private List<CartItemDto> items;
    /** Mã coupon khách nhập ở ô "Mã giảm giá" lúc thanh toán (có thể để trống). */
    private String code;
}
