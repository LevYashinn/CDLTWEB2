package com.fashionstore.promotion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateCartResponse {
    private BigDecimal subtotal;

    /** Tổng tiền giảm từ các khuyến mãi tự động (theo sản phẩm/danh mục). */
    private BigDecimal autoDiscount;

    /** Tiền giảm từ mã coupon (nếu có nhập và hợp lệ). */
    private BigDecimal couponDiscount;

    private BigDecimal total;

    /** true nếu mã coupon nhập vào hợp lệ và đã được áp dụng. */
    private boolean couponApplied;

    /** Thông báo cho người dùng, vd lý do mã không áp dụng được. */
    private String message;
}
